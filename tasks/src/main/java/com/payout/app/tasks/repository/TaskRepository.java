package com.payout.app.tasks.repository;

import com.payout.app.tasks.entity.Task;
import com.payout.app.tasks.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompanyId(Long companyId);

    List<Task> findByAssignedToId(Long contractorId);

    List<Task> findByAssignedToIsNullAndStatus(TaskStatus status);


    // задачи контрактора + открытый пул одним запросом
    @Query("""
        SELECT t FROM Task t
        WHERE t.assignedTo.id = :contractorId
        OR (t.assignedTo IS NULL AND t.status = 'CREATED')
    """)
    List<Task> findAvailableForContractor(@Param("contractorId") Long contractorId);



}