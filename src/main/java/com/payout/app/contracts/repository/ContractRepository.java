package com.payout.app.contracts.repository;


import com.payout.app.contracts.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByCompanyId(Long companyId);
    List<Contract> findByContractorId(Long contractorId);

    Optional<Contract> findByIdAndCompanyId(Long id, Long companyId);
    Optional<Contract> findByIdAndContractorId(Long id, Long contractorId);

    long countByCompanyId(Long companyId);

}
