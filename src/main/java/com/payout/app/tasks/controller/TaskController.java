package com.payout.app.tasks.controller;


import com.payout.app.iam.entity.User;
import com.payout.app.tasks.dto.TaskCreateRequest;
import com.payout.app.tasks.dto.TaskResponse;
import com.payout.app.tasks.service.TaskService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> create(@AuthenticationPrincipal User user, @Valid @RequestBody TaskCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.create(user, request));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.listForUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(user, id));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<TaskResponse> accept(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(taskService.accept(user, id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<TaskResponse> approve(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(taskService.approve(user, id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<TaskResponse> reject(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return ResponseEntity.ok(taskService.reject(user, id));
    }

}
