package com.payout.app.tasks.dto;


import com.payout.app.tasks.entity.TaskStatus;
import jakarta.validation.constraints.DecimalMin;
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
public class TaskResponse {

    private Long id;
    private Long companyId;
    private Long createdById;
    private Long assignedToId;
    private Long contractId;
    private String title;
    private String description;
    private BigDecimal budget;
    private OffsetDateTime deadline;
    private TaskStatus status;
    private OffsetDateTime createdAt;
}