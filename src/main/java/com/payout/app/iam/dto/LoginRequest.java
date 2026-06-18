package com.payout.app.iam.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
class LoginRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}