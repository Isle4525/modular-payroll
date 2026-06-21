package com.payout.app.contracts.controller;


import com.payout.app.contracts.dto.ContractCreateRequest;
import com.payout.app.contracts.dto.ContractResponse;
import com.payout.app.contracts.entity.Contract;
import com.payout.app.contracts.service.ContractService;
import com.payout.app.iam.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractorController {
    private final ContractService contractService;

    @PostMapping
    public ResponseEntity<ContractResponse> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ContractCreateRequest request){
        ContractResponse response = contractService.create(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<List<ContractResponse>> list(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(contractService.listForUser(user));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ContractResponse> getById(@AuthenticationPrincipal User user, @PathVariable Long id){
        return ResponseEntity.ok(contractService.getById(user, id));
    }

    @PostMapping("/{id}/sign")
    public ResponseEntity<ContractResponse> sign(@AuthenticationPrincipal User user, @PathVariable Long id){
        return ResponseEntity.ok(contractService.sign(user, id));
    }

}
