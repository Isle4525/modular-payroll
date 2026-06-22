package com.payout.app.contracts.service;


import com.payout.app.contracts.dto.ContractCreateRequest;
import com.payout.app.contracts.dto.ContractResponse;
import com.payout.app.contracts.entity.Contract;
import com.payout.app.contracts.entity.ContractStatus;
import com.payout.app.contracts.entity.ContractTemplate;
import com.payout.app.contracts.repository.ContractRepository;
import com.payout.app.contracts.repository.ContractTemplateRepository;
import com.payout.app.iam.entity.User;
import com.payout.app.iam.entity.UserRole;
import com.payout.app.iam.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ContractTemplateRepository contractTemplateRepository;
    private final UserRepository userRepository;

    @Transactional
    public ContractResponse create(User currentUser, ContractCreateRequest request) {
        User contracor = userRepository.findById(request.getContractorId())
                .filter(u -> u.getRole() == UserRole.CONTRACTOR)
                .orElseThrow(() -> new EntityNotFoundException("Contractor not found"));

        ContractTemplate template = null;
        if (request.getTemplateId() != null) {
            template = contractTemplateRepository.findById(request.getTemplateId())
                    .orElseThrow(() -> new EntityNotFoundException("Template not found"));
        }

        Contract contract = Contract.builder()
                .company(currentUser.getCompany())
                .template(template)
                .contractor(contracor)
                .contractNumber(generateContractNumber(currentUser.getCompany().getId()))
                .subject(request.getSubject())
                .amount(request.getAmount())
                .build();

        contractRepository.save(contract);
        return toResponse(contract);
    }


    public List<ContractResponse> listForUser(User user) {
        List<Contract> contracts;
        if (user.getRole() == UserRole.CONTRACTOR) {
            contracts = contractRepository.findByContractorId(user.getId());
        } else  {
            contracts = contractRepository.findByCompanyId(user.getCompany().getId());
        }

        return contracts.stream().map(this::toResponse).toList();
    }



    public ContractResponse getById(User user, Long id) {
        Contract contract = findAccessibleContract(user, id);
        return toResponse(contract);
    }


    @Transactional
    public ContractResponse sign(User user, Long contractId) {
        Contract contract = contractRepository.findByIdAndContractorId(contractId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("You can only sign your own contracts"));

        if (contract.getStatus() != ContractStatus.DRAFT && contract.getStatus() != ContractStatus.SENT){
            throw new IllegalStateException("Cannot sign contract with status '" + contract.getStatus() + "'");
        }

        contract.setStatus(ContractStatus.SIGNED);
        contract.setSignedAt(OffsetDateTime.now());
        contractRepository.save(contract);
        return toResponse(contract);

    }



    private Contract findAccessibleContract(User user, Long contractId) {
        if (user.getRole() == UserRole.CONTRACTOR) {
            return contractRepository.findByIdAndContractorId(contractId, user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Contract not found"));
        }

        return contractRepository.findByIdAndCompanyId(contractId, user.getCompany().getId())
                .orElseThrow(() -> new EntityNotFoundException("Contract not found"));
    }

    private String generateContractNumber(Long companyId) {
        int year = Year.now().getValue();
        long count = contractRepository.countByCompanyId(companyId);
        return String.format("CMP%d-%d-%04d", companyId, year, count + 1);
    }

    private ContractResponse toResponse(Contract contract) {
        return ContractResponse.builder()
                .id(contract.getId())
                .contractNumber(contract.getContractNumber())
                .contractorId(contract.getContractor().getId())
                .subject(contract.getSubject())
                .amount(contract.getAmount())
                .status(contract.getStatus())
                .signedAt(contract.getSignedAt())
                .fileUrl(contract.getFileUrl())
                .createdAt(contract.getCreatedAt())
                .build();
    }
}
