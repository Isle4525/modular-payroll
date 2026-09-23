package com.payout.app.iam.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterCompanyRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;
    private String bin;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Pass must be at least 8")
    private String password;


}