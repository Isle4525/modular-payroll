package com.payout.app.iam.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class CompanyResponse {
    private Long id;
    private String name;
    private String bin;
}