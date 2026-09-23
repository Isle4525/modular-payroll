package com.payout.app.contracts.repository;


import com.payout.app.contracts.entity.ContractTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractTemplateRepository extends JpaRepository<ContractTemplate, Long> {
    List<ContractTemplate> findByCompanyId(Long companyId);

}
