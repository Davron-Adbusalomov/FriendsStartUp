# To'lov / Invoice tizimi — Frontend uchun qo'llanma

Bu hujjat markaz moliyaviy tizimining (o'quvchi to'lovlari, markaz chiqimlari, hisobotlar)
to'liq ish oqimini va API'larni tushuntiradi. Backend paketi: `com.example.demo.payment`.

---

## 1. Asosiy tushunchalar (lug'at)

| Atama | Ma'nosi |
|---|---|
| **Invoice** | "Majburiyat" — kimga (yoki kimdan) qancha pul kerakligi haqidagi yozuv. Hozircha faqat **STUDENT_TUITION** (o'quvchi oylik to'lovi) avtomatik yaratiladi. `TEACHER_SALARY`/`VENDOR_BILL`/`OTHER` turlari kodda bor, lekin hali ishlatilmaydi (pastga qarang). |
| **Payment** | Haqiqiy pul harakati yozuvi — kirim (`RECEIPT`), chiqim (`EXPENSE`) yoki qaytarish (`REFUND`). Bir xil jadval o'quvchi to'lovini ham, markaz chiqimini (ijara, oylik va h.k.) ham ifodalaydi — farqi `category`/`counterpartyType`da. |
| **PaymentAllocation** | `Payment` va `Invoice` orasidagi bog'lanish (qaysi to'lov qaysi invoice'ni qancha yopgani). Bir to'lov bir necha invoice'ga bo'linib yozilishi mumkin. |
| **Allocation (bog'lash)** | **Har doim ataylab va aniq bo'ladi — hech qachon taxmin qilinmaydi.** To'lov yaratilganda default holatda hech qanday invoice'ga bog'lanmaydi. |
| **Balans** | O'quvchining "kredit" (ortiqcha to'lagan, hali hech qayerga bog'lanmagan pul) va "qarz" (to'lanmagan invoice) farqi. Faqat **admin ataylab bosgandagina** balansdagi kredit biror invoice'ga o'tkaziladi — avtomatik hech qachon emas. |
| **Sinov darslar (trial)** | Guruhga yangi qo'shilgan o'quvchiga berilgan bepul dars soni (`Grouping.trialLessonsCount`). Sinov davom etayotgan oy uchun invoice `amount=0`, `isTrial=true` bilan yaraladi. |

---

## 2. To'liq ish oqimi (flow)

### 2.1. Sozlash (admin bir marta qiladi)
1. **Guruh narxi** — `Grouping.monthlyFee` (guruh yaratish/tahrirlash formasida, `GroupDTO.monthlyFee`).
2. **Sinov darslar soni** (ixtiyoriy) — `Grouping.trialLessonsCount` (`GroupDTO.trialLessonsCount`).
3. **Individual chegirma** (ixtiyoriy, alohida o'quvchi uchun) — `Enrollment.customFee` (`EnrollmentDTO.customFee`) — berilgan bo'lsa, guruh narxidan ustun turadi.
4. **Markazning invoice generatsiya kuni** — `PUT /invoices/settings/billing-day` (default: har oyning 1-kuni).

### 2.2. Sinov darslarning kamayishi (avtomatik, frontend hech narsa qilmaydi)
O'quvchi `Enrollment` yaratilganda `trialLessonsGranted` guruh siyosatidan nusxa olinadi. Har safar
davomat (`Attendance`) `PRESENT`/`LATE` deb belgilanganda, agar sinov tugamagan bo'lsa,
`trialLessonsUsed` avtomatik +1 bo'ladi. Bu butunlay backend ichida, frontend uchun shaffof.

### 2.3. Invoice generatsiyasi
- **Avtomatik**: har kuni 01:00da tekshiriladi, markazning `billingDay`siga to'g'ri kelgan kunda
  shu oy uchun barcha faol (`APPROVED`) enrollmentlar bo'yicha `STUDENT_TUITION` invoice yaratiladi.
- **Qo'lda**: `POST /invoices/generate?month=yyyy-MM` — admin xohlagan vaqtda (masalan yangi
  qo'shilgan o'quvchi uchun) qayta chaqirishi mumkin. **Xavfsiz** — bir marta yaratilgan invoice
  qayta yaratilmaydi (idempotent).
- **Sinov davomida**: invoice baribir yaraladi, lekin `amount=0`, `invoiceStatus=PAID`, `isTrial=true`.
- **Teacher salary hozircha o'chirilgan** — oylik qanday hisoblanishi (fixed / dars soniga / % dan)
  hali qat'iy emas, shu sababli avtomatik generatsiya qilinmaydi.

### 2.4. To'lov yaratish (`POST /payments`)
Har bir to'lov `type` (`RECEIPT`/`EXPENSE`), `category` (masalan `TUITION_FEE`, `RENT`, ...),
`counterpartyType` (`STUDENT`/`TEACHER`/`VENDOR`/`OTHER`) bilan yoziladi. Allocation uchun **3 rejim**:

| Rejim | Qanday so'raladi | Natija |
|---|---|---|
| **Bog'lanmagan** (default) | `allocations` bo'sh, `autoAllocate` yo'q/`false` | To'lov yoziladi, hech qanday invoice'ga tegmaydi. Kelajakda ham avtomatik tegmaydi. |
| **Qo'lda (advanced)** | `allocations: [{invoiceId, amount}, ...]` | Faqat ko'rsatilgan invoice(lar)ga, ko'rsatilgan summada. |
| **Avtomatik FIFO** | `autoAllocate: true` (allocations bo'sh) | **Hozir mavjud** (`PENDING`/`PARTIALLY_PAID`) invoicelarga eng eskisidan boshlab yopiladi. Kelajakdagi invoice'larga hech qachon tegmaydi. |

Ad-hoc chiqimlar (ijara, komunal, marketing va h.k.) uchun Invoice umuman kerak emas — shunchaki
`type=EXPENSE`, mos `category` bilan `POST /payments` yuboriladi, allocation bo'sh qoladi.

### 2.5. Keyinroq bog'lash (agar kerak bo'lsa)
- `POST /payments/{id}/allocate` — mavjud, bog'lanmagan to'lovni admin keyinroq ma'lum bir
  invoice'ga qo'lda bog'laydi.
- `POST /invoices/{id}/apply-credit` — aksincha yo'nalish: admin biror invoice'ni ochib, "bu
  o'quvchining bog'lanmagan kredit puli bor, shuni shu invoice'ga hisoblayman" deb ataylab bosadi.

> **Muhim**: ikkisi ham faqat admin bosgandagina ishlaydi. Hech qanday fon jarayoni (invoice
> generatsiyasi, boshqa to'lov) buni o'zi hal qilmaydi.

### 2.6. Balans ko'rish
`GET /payments/balance?studentId=X` — har guruh bo'yicha (`credit`, `owed`, `netBalance`) va
umumiy (`totalCredit`, `totalOwed`, `netBalance`) qaytaradi. `netBalance > 0` — balansda pul bor,
`< 0` — qarz bor. To'lov formasini ochishdan oldin shu endpoint chaqirilib, kassirga ko'rsatilishi
tavsiya etiladi — shunda admin balansni ko'rib, kerak bo'lsa yuqoridagi `apply-credit`/`allocate`
tugmalarini bosadi.

### 2.7. Qaytarish (refund) va bekor qilish (void)
- **Refund** (`POST /payments/{id}/refund`) — pul haqiqatan qaytarilganda (masalan o'quvchi
  guruhni tashlab, puli qaytarildi). Yangi `REFUND` turdagi to'lov yaratiladi, tegishli invoice
  allocationlari mos ravishda ortga qaytariladi (invoice yana `PENDING`/`PARTIALLY_PAID` bo'lishi
  mumkin). Qisman refund ham mumkin (`amount` — to'lovning qolgan qaytarilmagan qismidan oshmasligi kerak).
- **Void** (`POST /payments/{id}/void`) — pul qaytarilmagan, faqat **xato kiritilgan yozuvni**
  bekor qilish uchun (masalan noto'g'ri summa kiritilgan). Hisobotlarda umuman ko'rinmaydi.

### 2.8. Hisobotlar (faqat DIRECTOR/SUPER_ADMIN)
- `GET /finance/summary?from=&to=` — davr bo'yicha kirim/chiqim/net cash flow, usul (CASH/CARD)
  va kategoriya bo'yicha taqsimot, umumiy qarzdorlik.
- `GET /finance/debtors` — qarzdor o'quvchilar ro'yxati (kim, qancha, nechta muddati o'tgan invoice).

---

## 3. Ruxsatlar (kim nima qila oladi)

| Amal | ADMIN | DIRECTOR / SUPER_ADMIN | TEACHER / STUDENT |
|---|---|---|---|
| To'lov qilish, ro'yxat, balans ko'rish | ✅ | ✅ | ❌ |
| Invoice ko'rish, generatsiya qilish | ✅ | ✅ | ❌ |
| Invoice bekor qilish, refund, void, billing sozlamalari | ✅ | ✅ | ❌ |
| **Finance summary, debtors (hisobotlar)** | ❌ | ✅ | ❌ |

Frontendda: ADMIN roli uchun hisobot sahifalarini (`/finance/summary`, `/finance/debtors`)
umuman ko'rsatmaslik kerak — backend ham `403` qaytaradi, lekin UI darajasida ham yashirish tavsiya etiladi.

---

## 4. API Reference

### 4.1. Invoices — `api/v1/invoices`

| Method & Path | Ruxsat | Vazifa |
|---|---|---|
| `GET /` | `GET_INVOICES_LIST` | Invoice ro'yxati. Filter: `type`, `userId`, `groupId`, `status`, `period` (`yyyy-MM-dd`, oyning 1-kuni). Sahifalangan (`Pageable`). |
| `GET /{id}` | `GET_INVOICE` | Bitta invoice tafsiloti. |
| `POST /generate?month=yyyy-MM` | `GENERATE_INVOICES` | Joriy center uchun berilgan oy (default: joriy oy) `STUDENT_TUITION` invoicelarini yaratadi. Idempotent — qayta chaqirish xavfsiz. Javob: yaratilgan invoice soni (`int`). |
| `POST /{id}/cancel` | `CANCEL_INVOICE` | Invoice statusini `CANCELLED` qiladi. |
| `POST /{id}/apply-credit` | `CREATE_PAYMENT` | Shu student/teacherning bog'lanmagan (unallocated) oldingi to'lovlarini ataylab shu invoice'ga qo'llaydi. |
| `GET /settings/billing-day` | `GET_BILLING_SETTINGS` | Joriy centerning invoice generatsiya kunini (`1-31`) qaytaradi. |
| `PUT /settings/billing-day` | `UPDATE_BILLING_SETTINGS` | Body: `{ "billingDay": 5 }`. Qiymat `1-31` oralig'ida bo'lishi kerak. |

**InvoiceDTO** (javob shakli):
```json
{
  "id": "uuid",
  "type": "STUDENT_TUITION",
  "userId": 123,
  "groupId": "uuid",
  "groupName": "Matematika-1",
  "period": "2026-08-01",
  "amount": 500000,
  "discountAmount": 0,
  "dueDate": "2026-08-01",
  "invoiceStatus": "PENDING",
  "isTrial": false,
  "centerId": "uuid"
}
```

### 4.2. Payments — `api/v1/payments`

| Method & Path | Ruxsat | Vazifa |
|---|---|---|
| `POST /` | `CREATE_PAYMENT` | Yangi kirim/chiqim yozadi (pastga qarang: `CreatePaymentRequest`). |
| `GET /` | `GET_PAYMENTS_LIST` | To'lovlar ro'yxati. Filter: `type`, `category`, `userId`, `method`, `status`, `from`/`to` (`paidAt` bo'yicha). |
| `POST /{id}/refund` | `REFUND_PAYMENT` | Body: `{ "amount": 100000, "note": "..." }`. To'liq/qisman qaytarish. |
| `POST /{id}/void` | `VOID_PAYMENT` | Xato yozuvni bekor qilish (pul qaytmagan). |
| `POST /{id}/allocate` | `CREATE_PAYMENT` | Body: `{ "invoiceId": "uuid", "amount": 100000 }`. Bog'lanmagan to'lovni qo'lda invoice'ga bog'lash. |
| `GET /balance?studentId=X` | `GET_PAYMENTS_LIST` | O'quvchi balansi (pastga qarang: `StudentBalanceResponse`). |

**CreatePaymentRequest** (so'rov shakli):
```json
{
  "type": "RECEIPT",
  "category": "TUITION_FEE",
  "counterpartyType": "STUDENT",
  "userId": 123,
  "groupId": "uuid",
  "amount": 500000,
  "method": "CASH",
  "paidAt": "2026-08-22T10:00:00",
  "note": "Avgust oyi uchun",
  "allocations": [],
  "autoAllocate": false
}
```
- Ad-hoc chiqim misoli (ijara): `type=EXPENSE`, `category=RENT`, `counterpartyType=VENDOR`,
  `vendorName="..."`, `userId`/`groupId` bo'sh, `allocations` bo'sh.
- O'qituvchi bilan bog'liq to'lov (kelajakda): `counterpartyType=TEACHER`, `userId=teacherId`.

**PaymentDTO** (javob shakli): `id, type, direction (+1/-1), category, counterpartyType, userId,
groupId, vendorName, amount, method, paidAt, processedBy, note, paymentStatus, relatedPaymentId, centerId`.

**StudentBalanceResponse**:
```json
{
  "studentId": 123,
  "totalCredit": 200000,
  "totalOwed": 500000,
  "netBalance": -300000,
  "groups": [
    { "groupId": "uuid", "groupName": "Matematika-1", "credit": 200000, "owed": 500000, "netBalance": -300000 }
  ]
}
```

### 4.3. Finance reports — `api/v1/finance`

| Method & Path | Ruxsat | Vazifa |
|---|---|---|
| `GET /summary?from=&to=` | `GET_FINANCE_SUMMARY` | `FinanceSummaryResponse`: `totalIncome`, `totalExpense`, `netCashFlow`, `totalOutstanding`, `incomeByMethod` (CASH/CARD/BANK_TRANSFER bo'yicha), `amountByCategory`. |
| `GET /debtors` | `GET_DEBTORS_LIST` | `DebtorResponse[]`: `studentId`, `studentFullName`, `totalOwed`, `overdueInvoiceCount`. |

---

## 5. Enum qiymatlari (frontend select/dropdown uchun)

- **PaymentType**: `RECEIPT`, `EXPENSE`, `REFUND`
- **PaymentMethod**: `CASH`, `CARD`, `BANK_TRANSFER`
- **PaymentStatus**: `COMPLETED`, `VOID`
- **FinanceCategory**: `TUITION_FEE`, `TEACHER_SALARY`, `RENT`, `UTILITIES`, `MARKETING`, `MAINTENANCE`, `EQUIPMENT`, `TAX`, `OTHER_INCOME`, `OTHER_EXPENSE`
- **CounterpartyType**: `STUDENT`, `TEACHER`, `VENDOR`, `OTHER`
- **InvoiceType**: `STUDENT_TUITION`, `TEACHER_SALARY` (hozircha ishlatilmaydi), `VENDOR_BILL` (hozircha ishlatilmaydi), `OTHER`
- **InvoiceStatus**: `PENDING`, `PARTIALLY_PAID`, `PAID`, `CANCELLED`
