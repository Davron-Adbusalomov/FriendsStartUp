package com.example.demo.payment.service;

import com.example.demo.enums.AttendanceStatus;
import com.example.demo.enums.InvoiceStatus;
import com.example.demo.enums.InvoiceType;
import com.example.demo.management.model.Center;
import com.example.demo.management.model.Grouping;
import com.example.demo.management.model.Student;
import com.example.demo.management.model.Teacher;
import com.example.demo.management.repository.AttendanceRepository;
import com.example.demo.management.repository.CenterRepository;
import com.example.demo.management.repository.GroupRepository;
import com.example.demo.management.repository.TeacherRepository;
import com.example.demo.payment.model.Invoice;
import com.example.demo.payment.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceGenerationService {

    private static final List<AttendanceStatus> TRIAL_CONSUMING_STATUSES = List.of(AttendanceStatus.PRESENT, AttendanceStatus.LATE);

    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final CenterRepository centerRepository;
    private final InvoiceRepository invoiceRepository;
    private final AttendanceRepository attendanceRepository;

    /**
     * Har kuni 01:00 (server GMT+5) ishlaydi, lekin har center faqat o'zining {@code billingDay}
     * kunida generatsiya qilinadi (default 1). Oyda shu kun bo'lmasa (masalan billingDay=31,
     * fevral 28/29 kun) — oyning oxirgi kuniga tushiriladi. Bir necha marta ishga tushsa ham
     * xavfsiz: idempotentlik {@code generateMonthly} ichida (exists tekshiruvi + DB unique indeks) ta'minlanadi.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateForAllCentersScheduled() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);

        for (Center center : centerRepository.findAll()) {
            int billingDay = center.getBillingDay() != null ? center.getBillingDay() : 1;
            int effectiveDay = Math.min(billingDay, currentMonth.lengthOfMonth());
            if (today.getDayOfMonth() == effectiveDay) {
                generateMonthly(center.getId(), currentMonth);
            }
        }
    }

    /** Admin tomonidan qo'lda chaqiriladi — bitta center uchun (kerak bo'lsa o'tgan/kelasi oy uchun ham). */
    @Transactional
    public int generateMonthly(UUID centerId, YearMonth month) {
        // Teacher.monthlySalary hozircha faqat "fixed oylik" — o'qituvchi haqi qanday hisoblanishi
        // (dars soniga, guruh to'lovidan foizga va h.k.) hali qat'iy belgilanmagan, shu sababli
        // TEACHER_SALARY invoice avtomatik generatsiyadan chiqarib qo'yilgan (generateTeacherSalaryInvoices
        // metodi joyida qoladi — usul aniqlanganda shu yerda qayta yoqiladi).
        return generateStudentTuitionInvoices(centerId, month);
    }

    /**
     * Student-guruh bog'lanishi {@code group_student} jadvali (Grouping.students) orqali yuritiladi —
     * bu yerda ham shu manbadan foydalaniladi (Enrollment jadvali real assign-student oqimida
     * hech qachon to'ldirilmaydi, shu sabab u yerdan o'qish invoice generatsiyasini doim bo'sh qoldirar edi).
     * Trial (sinov) darslar ham shu sababli statik counterga emas, balki haqiqiy {@code attendance}
     * tarixiga qarab hisoblanadi — pastdagi {@link #isStillOnTrial} metodiga qarang.
     */
    private int generateStudentTuitionInvoices(UUID centerId, YearMonth month) {
        LocalDate period = month.atDay(1);
        int count = 0;
        List<Grouping> groups = groupRepository.findByCenterId(centerId);

        for (Grouping group : groups) {
            BigDecimal fee = group.getMonthlyFee();
            if (fee == null) {
                log.warn("Guruh '{}' (id={}) uchun monthlyFee belgilanmagan, invoice yaratilmadi", group.getName(), group.getId());
                continue;
            }

            for (Student student : group.getStudents()) {
                if (invoiceRepository.existsByTypeAndUserIdAndGroupIdAndPeriod(
                        InvoiceType.STUDENT_TUITION, student.getId(), group.getId(), period)) {
                    continue;
                }


                boolean onTrial = isStillOnTrial(student.getId(), group, period);

                Invoice invoice = new Invoice();
                invoice.setType(InvoiceType.STUDENT_TUITION);
                invoice.setUserId(student.getId());
                invoice.setGroupId(group.getId());
                invoice.setPeriod(period);
                invoice.setDueDate(period);
                invoice.setCenterId(centerId);
                invoice.setIsTrial(onTrial);

                if (onTrial) {
                    invoice.setAmount(BigDecimal.ZERO);
                    invoice.setInvoiceStatus(InvoiceStatus.PAID);
                } else {
                    invoice.setAmount(fee);
                    invoice.setInvoiceStatus(InvoiceStatus.PENDING);
                }

                invoiceRepository.save(invoice);
                count++;
                // E'TIBOR: bu yerda avvalgi ortiqcha to'lov avtomatik "credit" sifatida qo'llanilmaydi —
                // qaysi pul aslida nimaga mo'ljallangani noaniq bo'lishi mumkin. Agar student haqiqatan
                // ham oldindan to'lagan bo'lsa, admin buni ko'rib (balans/to'lovlar orqali) ataylab
                // POST /payments/{id}/allocate bilan shu invoice'ga bog'laydi.
            }
        }
        return count;
    }

    /**
     * Statik counter (avvalgi Enrollment.trialLessonsUsed) o'rniga real {@code attendance} tarixidan
     * hisoblanadi: student shu guruhda, shu davrgacha nechta darsga PRESENT/LATE belgilangan bo'lsa,
     * shuni guruhning {@code trialLessonsCount}i bilan solishtiradi. Hech qanday qo'shimcha jadval/ustun
     * kerak emas — group_student hali ham yagona haqiqat manbai bo'lib qoladi.
     * <p>
     * Public: invoice generatsiyasidan tashqari (masalan attendance matrix'da studentType'ni
     * hisoblash uchun) ham shared holda ishlatiladi.
     */
    public boolean isStillOnTrial(Long studentId, Grouping group, LocalDate period) {
        Integer trialLessonsCount = group.getTrialLessonsCount();
        if (trialLessonsCount == null || trialLessonsCount <= 0) {
            return false;
        }
        long attended = attendanceRepository.countByStudentIdAndGroupIdAndAttendanceStatusInAndAttendanceTimeBefore(
                studentId, group.getId(), TRIAL_CONSUMING_STATUSES, period.plusMonths(1).atStartOfDay());
        return attended < trialLessonsCount;
    }

    private int generateTeacherSalaryInvoices(UUID centerId, YearMonth month) {
        LocalDate period = month.atDay(1);
        int count = 0;
        List<Teacher> teachers = teacherRepository.findByCenterIdAndMonthlySalaryIsNotNull(centerId);

        for (Teacher teacher : teachers) {
            if (invoiceRepository.existsByTypeAndUserIdAndPeriod(InvoiceType.TEACHER_SALARY, teacher.getId(), period)) {
                continue;
            }

            Invoice invoice = new Invoice();
            invoice.setType(InvoiceType.TEACHER_SALARY);
            invoice.setUserId(teacher.getId());
            invoice.setPeriod(period);
            invoice.setDueDate(period);
            invoice.setAmount(teacher.getMonthlySalary());
            invoice.setInvoiceStatus(InvoiceStatus.PENDING);
            invoice.setCenterId(centerId);
            invoice = invoiceRepository.save(invoice);
            count++;
            // Bu yerda ham avtomatik credit-qo'llash yo'q — yuqoridagi izohga qarang.
        }
        return count;
    }
}
