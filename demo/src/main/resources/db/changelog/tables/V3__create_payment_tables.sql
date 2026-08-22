-- Billing/finance domain: student tuition, teacher salary, vendor bills, cash movements (kirim/chiqim/vozvrat)

ALTER TABLE groups ADD COLUMN monthly_fee NUMERIC(12, 2);
ALTER TABLE groups ADD COLUMN trial_lessons_count INTEGER;

ALTER TABLE enrollment ADD COLUMN custom_fee NUMERIC(12, 2);
ALTER TABLE enrollment ADD COLUMN trial_lessons_granted INTEGER DEFAULT 0;
ALTER TABLE enrollment ADD COLUMN trial_lessons_used INTEGER DEFAULT 0;

ALTER TABLE teacher ADD COLUMN monthly_salary NUMERIC(12, 2);

ALTER TABLE center ADD COLUMN billing_day INTEGER DEFAULT 1;

CREATE TABLE invoice
(
    id               UUID                        NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    status           VARCHAR(255)                NOT NULL,
    created_by       BIGINT,
    updated_by       BIGINT,
    type             VARCHAR(30)                 NOT NULL,
    user_id          BIGINT,
    group_id         UUID,
    vendor_name      VARCHAR(255),
    period           DATE,
    amount           NUMERIC(12, 2)              NOT NULL,
    discount_amount  NUMERIC(12, 2)               DEFAULT 0,
    due_date         DATE,
    description      VARCHAR(1000),
    invoice_status   VARCHAR(30)                  DEFAULT 'PENDING',
    is_trial         BOOLEAN                      DEFAULT FALSE,
    center_id        UUID                        NOT NULL,
    CONSTRAINT pk_invoice PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uq_invoice_tuition ON invoice (user_id, group_id, period) WHERE type = 'STUDENT_TUITION';
CREATE UNIQUE INDEX uq_invoice_salary ON invoice (user_id, period) WHERE type = 'TEACHER_SALARY';

CREATE TABLE payment
(
    id                 UUID                        NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    status             VARCHAR(255)                NOT NULL,
    created_by         BIGINT,
    updated_by         BIGINT,
    type               VARCHAR(20)                 NOT NULL,
    direction          SMALLINT                    NOT NULL,
    category           VARCHAR(30)                 NOT NULL,
    counterparty_type  VARCHAR(20)                 NOT NULL,
    user_id            BIGINT,
    group_id           UUID,
    vendor_name        VARCHAR(255),
    amount             NUMERIC(12, 2)              NOT NULL,
    method             VARCHAR(20)                 NOT NULL,
    paid_at            TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    processed_by       BIGINT,
    note               VARCHAR(1000),
    payment_status     VARCHAR(20)                  DEFAULT 'COMPLETED',
    related_payment_id UUID,
    center_id          UUID                        NOT NULL,
    CONSTRAINT pk_payment PRIMARY KEY (id)
);

CREATE TABLE payment_allocation
(
    id               UUID                        NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    status           VARCHAR(255)                NOT NULL,
    created_by       BIGINT,
    updated_by       BIGINT,
    payment_id       UUID                        NOT NULL,
    invoice_id       UUID                        NOT NULL,
    allocated_amount NUMERIC(12, 2)              NOT NULL,
    center_id        UUID                        NOT NULL,
    CONSTRAINT pk_payment_allocation PRIMARY KEY (id)
);

ALTER TABLE invoice
    ADD CONSTRAINT FK_INVOICE_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE invoice
    ADD CONSTRAINT FK_INVOICE_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

ALTER TABLE invoice
    ADD CONSTRAINT FK_INVOICE_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_GROUP FOREIGN KEY (group_id) REFERENCES groups (id);

ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE payment
    ADD CONSTRAINT FK_PAYMENT_ON_RELATED_PAYMENT FOREIGN KEY (related_payment_id) REFERENCES payment (id);

ALTER TABLE payment_allocation
    ADD CONSTRAINT FK_PAYMENT_ALLOCATION_ON_CENTER FOREIGN KEY (center_id) REFERENCES center (id);

ALTER TABLE payment_allocation
    ADD CONSTRAINT FK_PAYMENT_ALLOCATION_ON_PAYMENT FOREIGN KEY (payment_id) REFERENCES payment (id);

ALTER TABLE payment_allocation
    ADD CONSTRAINT FK_PAYMENT_ALLOCATION_ON_INVOICE FOREIGN KEY (invoice_id) REFERENCES invoice (id);

INSERT INTO default_permission_entity (name, description)
VALUES ('CREATE_INVOICE', 'Create a finance invoice/obligation'),
       ('GET_INVOICE', 'View a single invoice'),
       ('GET_INVOICES_LIST', 'View list of invoices'),
       ('GENERATE_INVOICES', 'Manually trigger monthly invoice generation'),
       ('CANCEL_INVOICE', 'Cancel an invoice'),
       ('CREATE_PAYMENT', 'Record a cash receipt or expense'),
       ('GET_PAYMENT', 'View a single payment'),
       ('GET_PAYMENTS_LIST', 'View list of payments'),
       ('REFUND_PAYMENT', 'Refund a completed payment'),
       ('VOID_PAYMENT', 'Void an incorrectly recorded payment'),
       ('GET_FINANCE_SUMMARY', 'View income/expense finance summary'),
       ('GET_DEBTORS_LIST', 'View list of students with outstanding tuition'),
       ('UPDATE_BILLING_SETTINGS', 'Update the day of month invoices are auto-generated on'),
       ('GET_BILLING_SETTINGS', 'View the current billing-day setting')
ON CONFLICT (name) DO NOTHING;

-- DIRECTOR va SUPER_ADMIN — moliyaning barcha qismi: operatsion + hisobot/tuzatish/sozlamalar.
INSERT INTO role_default_permissions (role_id, default_permission_name)
VALUES ('DIRECTOR', 'CREATE_INVOICE'),
       ('DIRECTOR', 'GET_INVOICE'),
       ('DIRECTOR', 'GET_INVOICES_LIST'),
       ('DIRECTOR', 'GENERATE_INVOICES'),
       ('DIRECTOR', 'CANCEL_INVOICE'),
       ('DIRECTOR', 'CREATE_PAYMENT'),
       ('DIRECTOR', 'GET_PAYMENT'),
       ('DIRECTOR', 'GET_PAYMENTS_LIST'),
       ('DIRECTOR', 'REFUND_PAYMENT'),
       ('DIRECTOR', 'VOID_PAYMENT'),
       ('DIRECTOR', 'GET_FINANCE_SUMMARY'),
       ('DIRECTOR', 'GET_DEBTORS_LIST'),
       ('DIRECTOR', 'UPDATE_BILLING_SETTINGS'),
       ('DIRECTOR', 'GET_BILLING_SETTINGS'),
       ('SUPER_ADMIN', 'CREATE_INVOICE'),
       ('SUPER_ADMIN', 'GET_INVOICE'),
       ('SUPER_ADMIN', 'GET_INVOICES_LIST'),
       ('SUPER_ADMIN', 'GENERATE_INVOICES'),
       ('SUPER_ADMIN', 'CANCEL_INVOICE'),
       ('SUPER_ADMIN', 'CREATE_PAYMENT'),
       ('SUPER_ADMIN', 'GET_PAYMENT'),
       ('SUPER_ADMIN', 'GET_PAYMENTS_LIST'),
       ('SUPER_ADMIN', 'REFUND_PAYMENT'),
       ('SUPER_ADMIN', 'VOID_PAYMENT'),
       ('SUPER_ADMIN', 'GET_FINANCE_SUMMARY'),
       ('SUPER_ADMIN', 'GET_DEBTORS_LIST'),
       ('SUPER_ADMIN', 'UPDATE_BILLING_SETTINGS'),
       ('SUPER_ADMIN', 'GET_BILLING_SETTINGS'),
       -- ADMIN — moliyaning operatsion qismi: to'lov qilish/ko'rish, invoice generatsiya/bekor qilish,
       -- qaytarish/void, billing sozlamalari. Faqat qimmatli hisobotlar (GET_FINANCE_SUMMARY,
       -- GET_DEBTORS_LIST) ADMIN'ga berilmaydi — faqat DIRECTOR/SUPER_ADMIN ko'ra oladi.
       ('ADMIN', 'CREATE_PAYMENT'),
       ('ADMIN', 'GET_PAYMENT'),
       ('ADMIN', 'GET_PAYMENTS_LIST'),
       ('ADMIN', 'GET_INVOICE'),
       ('ADMIN', 'GET_INVOICES_LIST'),
       ('ADMIN', 'GENERATE_INVOICES'),
       ('ADMIN', 'CANCEL_INVOICE'),
       ('ADMIN', 'REFUND_PAYMENT'),
       ('ADMIN', 'VOID_PAYMENT'),
       ('ADMIN', 'UPDATE_BILLING_SETTINGS'),
       ('ADMIN', 'GET_BILLING_SETTINGS')
ON CONFLICT (role_id, default_permission_name) DO NOTHING;
