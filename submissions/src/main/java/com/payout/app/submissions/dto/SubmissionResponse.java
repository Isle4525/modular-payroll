package com.payout.app.submissions.dto;

import com.payout.app.submissions.entity.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private Long id;
    private Long taskId;
    private Long contractorId;
    private String content;
    private List<String> attachments;
    private SubmissionStatus status;
    private OffsetDateTime submittedAt;
}
