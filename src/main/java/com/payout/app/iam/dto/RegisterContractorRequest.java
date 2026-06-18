package com.payout.app.iam.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
class  RegisterContractorRequest {

    @NotBlank
    private String userame;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    private boolean selfEmployed;

}