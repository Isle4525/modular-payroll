package com.payout.app.iam.controller;


import com.payout.app.iam.dto.*;
import com.payout.app.iam.entity.User;
import com.payout.app.iam.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerCompany(@Valid @RequestBody RegisterCompanyRequest registerCompanyRequest) {
        AuthResponse authResponse = authService.registerCompany(registerCompanyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/register-contractor")
    public ResponseEntity<AuthResponse> registerContractor(@Valid @RequestBody RegisterContractorRequest registerContractorRequest) {
        AuthResponse authResponse = authService.registerContractor(registerContractorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.getCurrentUser(user));
    }

}
