package com.example.demo.payment.dto;

import com.example.demo.enums.CounterpartyType;
import com.example.demo.enums.FinanceCategory;
import com.example.demo.enums.PaymentMethod;
import com.example.demo.enums.PaymentType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * type=RECEIPT -> kirim (masalan o'quvchi to'lovi), type=EXPENSE -> chiqim
 * (o'qituvchi oyligi, ijara va h.k.). REFUND uchun alohida {@code RefundRequest} ishlatiladi.
 *
 * <p>Allocation (bu to'lov qaysi invoice(lar)ni yopishi) — HAR DOIM aniq va ataylab bo'lishi kerak,
 * hech narsa taxmin qilinmaydi:
 * <ul>
 *   <li>{@code allocations} to'ldirilgan bo'lsa — faqat shu invoice(lar)ga, ko'rsatilgan summada.</li>
 *   <li>{@code allocations} bo'sh va {@code autoAllocate=true} bo'lsa — hozir mavjud (PENDING/
 *       PARTIALLY_PAID) invoice(lar)ga FIFO (eng eski qarzdan) avtomatik yopiladi.</li>
 *   <li>Aks holda (default) — to'lov hech qaysi invoice'ga bog'lanmaydi, faqat yozib qo'yiladi.
 *       Kelajakda ham hech qanday invoice'ni avtomatik yopmaydi — admin xohlaganda
 *       {@code POST /payments/{id}/allocate} orqali qo'lda, ataylab allocate qiladi.</li>
 * </ul>
 */
@Getter
@Setter
public class CreatePaymentRequest {
    private PaymentType type;
    private FinanceCategory category;
    private CounterpartyType counterpartyType;
    private Long userId;
    private UUID groupId;
    private String vendorName;
    private BigDecimal amount;
    private PaymentMethod method;
    private LocalDateTime paidAt;
    private String note;
    private List<AllocationRequest> allocations;
    private Boolean autoAllocate = false;
}
