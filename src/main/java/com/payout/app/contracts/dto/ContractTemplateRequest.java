package com.payout.app.contracts.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContractTemplateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Body template is required")
    private String bodyTemplate;
}