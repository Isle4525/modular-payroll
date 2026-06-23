package com.payout.app.submissions.controller;


import com.payout.app.iam.entity.User;
import com.payout.app.submissions.dto.SubmissionRequest;
import com.payout.app.submissions.dto.SubmissionResponse;
import com.payout.app.submissions.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionResponse> submit(@AuthenticationPrincipal User user, @PathVariable Long taskId, @Valid @RequestBody SubmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(submissionService.submit(user, taskId, request));
    }

    @GetMapping
    public ResponseEntity<List<SubmissionResponse>> list(@AuthenticationPrincipal User user, @PathVariable Long taskId) {
        return ResponseEntity.ok(submissionService.listForTask(user, taskId));
    }


}
