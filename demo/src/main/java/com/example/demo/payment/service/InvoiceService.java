package com.example.demo.payment.service;

import com.example.demo.config.TenantContext;
import com.example.demo.enums.InvoiceStatus;
import com.example.demo.enums.InvoiceType;
import com.example.demo.management.model.Center;
import com.example.demo.management.repository.CenterRepository;
import com.example.demo.payment.dto.BillingSettingsDTO;
import com.example.demo.payment.dto.InvoiceDTO;
import com.example.demo.payment.mapper.InvoiceMapper;
import com.example.demo.payment.model.Invoice;
import com.example.demo.payment.repository.InvoiceRepository;
import com.example.demo.payment.specification.InvoiceSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final InvoiceGenerationService invoiceGenerationService;
    private final CenterRepository centerRepository;
    private final PaymentService paymentService;

    public Page<InvoiceDTO> getAll(
            InvoiceType type, Long userId, UUID groupId, InvoiceStatus status, LocalDate period, Pageable pageable
    ) {
        var spec = InvoiceSpecification.advancedFilter(TenantContext.getCenterId(), type, userId, groupId, status, period);
        return invoiceRepository.findAll(spec, pageable).map(invoiceMapper::toDto);
    }

    public InvoiceDTO getById(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + id));
        return invoiceMapper.toDto(invoice);
    }

    /** Admin tomonidan qo'lda trigger qilinadi — joriy center uchun berilgan oy majburiyatlarini yaratadi. */
    public int generate(YearMonth month) {
        return invoiceGenerationService.generateMonthly(TenantContext.getCenterId(), month);
    }

    @Transactional
    public InvoiceDTO cancel(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + id));
        invoice.setInvoiceStatus(InvoiceStatus.CANCELLED);
        return invoiceMapper.toDto(invoiceRepository.save(invoice));
    }

    /**
     * Admin ataylab bosadigan tugma: shu student/teacherning hali boshqa invoice'ga bog'lanmagan
     * (unallocated) oldingi to'lovlarini shu invoice'ga qo'llaydi. Hech qachon avtomatik chaqirilmaydi.
     */
    @Transactional
    public InvoiceDTO applyCredit(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Invoice not found with id: " + id));
        paymentService.applyAvailableCredit(invoice);
        return invoiceMapper.toDto(invoice);
    }

    public BillingSettingsDTO getBillingSettings() {
        Center center = currentCenter();
        BillingSettingsDTO dto = new BillingSettingsDTO();
        dto.setBillingDay(center.getBillingDay() != null ? center.getBillingDay() : 1);
        return dto;
    }

    @Transactional
    public BillingSettingsDTO updateBillingSettings(BillingSettingsDTO request) {
        if (request.getBillingDay() == null || request.getBillingDay() < 1 || request.getBillingDay() > 31) {
            throw new IllegalArgumentException("billingDay 1 dan 31 gacha bo'lishi kerak");
        }
        Center center = currentCenter();
        center.setBillingDay(request.getBillingDay());
        centerRepository.save(center);
        return getBillingSettings();
    }

    private Center currentCenter() {
        UUID centerId = TenantContext.getCenterId();
        return centerRepository.findById(centerId)
                .orElseThrow(() -> new EntityNotFoundException("Center not found with id: " + centerId));
    }
}
