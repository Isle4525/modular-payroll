package com.payout.app.payments.repository;

import com.payout.app.payments.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByContractorId(Long contractorId);

    List<Payment> findByTaskId(Long taskId);

    Optional<Payment> findByTaskIdAndContractId(Long taskId, Long contractId);

}
