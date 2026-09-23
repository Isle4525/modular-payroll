package com.payout.app.contracts.dto;

import com.payout.app.contracts.entity.ContractStatus;
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
public class ContractResponse {
    private Long id;
    private String contractNumber;
    private Long contractorId;
    private String subject;
    private BigDecimal amount;
    private ContractStatus status;
    private OffsetDateTime signedAt;
    private String fileUrl;
    private OffsetDateTime createdAt;
}