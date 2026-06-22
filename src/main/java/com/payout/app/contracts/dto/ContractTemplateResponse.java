package com.payout.app.contracts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractTemplateResponse {
    private Long id;
    private String name;
    private String bodyTemplate;
    private OffsetDateTime createdAt;
}