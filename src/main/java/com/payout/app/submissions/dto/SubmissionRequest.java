package com.payout.app.submissions.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SubmissionRequest {

    @NotBlank(message = "content is required")
    private String content;

    private List<String> attachments;
}
