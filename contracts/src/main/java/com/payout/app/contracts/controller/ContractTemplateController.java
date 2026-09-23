package com.payout.app.contracts.controller;


import com.payout.app.contracts.dto.ContractTemplateRequest;
import com.payout.app.contracts.dto.ContractTemplateResponse;
import com.payout.app.contracts.service.ContractTemplateService;
import com.payout.app.iam.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts/templates")
@RequiredArgsConstructor
public class ContractTemplateController {

    private final ContractTemplateService contractTemplateService;

    @PostMapping
    public ResponseEntity<ContractTemplateResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ContractTemplateRequest contractTemplateRequest
    ) {
        ContractTemplateResponse response = contractTemplateService.create(user, contractTemplateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ContractTemplateResponse>> list(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(contractTemplateService.listForCompany(user));
    }

}
