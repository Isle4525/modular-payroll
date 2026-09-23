package com.payout.app.payments.dto;

import com.payout.app.payments.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long taskId;
    private Long contractId;
    private Long contractorId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String providerTxId;
    private OffsetDateTime createdAt;
    private OffsetDateTime paidAt;
}