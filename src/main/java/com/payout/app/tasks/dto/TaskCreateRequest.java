package com.payout.app.tasks.dto;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class TaskCreateRequest {

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "Budget is required")
    @DecimalMin(value = "0.01", message = "Budget must be greater than 0")
    private BigDecimal budget;

    private OffsetDateTime deadline;

    private Long assignedToId;
}