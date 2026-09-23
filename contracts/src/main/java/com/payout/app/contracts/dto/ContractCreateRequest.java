package com.payout.app.contracts.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractCreateRequest {

    private Long templateId; // опционально

    @NotNull(message = "Contractor id is required")
    private Long contractorId;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
}