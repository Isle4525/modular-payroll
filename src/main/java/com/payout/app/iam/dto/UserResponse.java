package com.payout.app.iam.dto;


import com.payout.app.iam.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UserResponse {
    private Long id;
    private String email;
    private UserRole role;
    private boolean selfEmployed;
    private CompanyResponse company;

}