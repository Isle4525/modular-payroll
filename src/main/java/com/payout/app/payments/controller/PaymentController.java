package com.payout.app.payments.controller;


import com.payout.app.iam.entity.User;
import com.payout.app.payments.dto.PaymentResponse;
import com.payout.app.payments.entity.Payment;
import com.payout.app.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/tasks//{id}")
    public ResponseEntity<PaymentResponse> createPayment(@AuthenticationPrincipal User user, @PathVariable Long taskId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(user, taskId));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.listForUser(user));
    }

}
