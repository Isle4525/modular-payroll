package com.payout.app.contracts.service;


import com.payout.app.contracts.dto.ContractTemplateRequest;
import com.payout.app.contracts.dto.ContractTemplateResponse;
import com.payout.app.contracts.entity.ContractTemplate;
import com.payout.app.contracts.repository.ContractTemplateRepository;
import com.payout.app.iam.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractTemplateService {

    private final ContractTemplateRepository contractTemplateRepository;

    @CacheEvict(value = "contract-templates", key = "#user.company.id")
    public ContractTemplateResponse create(User user, ContractTemplateRequest request){
        ContractTemplate template = ContractTemplate.builder()
                .company(user.getCompany())
                .name(request.getName())
                .bodyTemplate(request.getBodyTemplate())
                .build();

        contractTemplateRepository.save(template);

        return toResponse(template);


    }

    @Cacheable(value = "contract-templates", key = "#user.company.id")
    public List<ContractTemplateResponse> listForCompany(User user){
        return contractTemplateRepository.findByCompanyId(user.getCompany().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ContractTemplateResponse toResponse(ContractTemplate template){
        return ContractTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .bodyTemplate(template.getBodyTemplate())
                .createdAt(template.getCreatedAt())
                .build();
    }

}
