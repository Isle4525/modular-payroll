package com.payout.app.submissions.repository;


import com.payout.app.submissions.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByTaskId(Long taskId);

    List<Submission> findByContractorId(Long contractorId);

    Optional<Submission> findByIdAndContractorId(Long id, Long contractorId);
}
