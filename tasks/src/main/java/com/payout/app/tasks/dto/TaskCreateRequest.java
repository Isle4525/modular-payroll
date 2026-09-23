package com.payout.app.tasks.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.apache.commons.lang3.time.TimeZones.GMT;

@Data
@NoArgsConstructor
@AllArgsConstructor
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