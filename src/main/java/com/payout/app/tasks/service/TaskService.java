package com.payout.app.tasks.service;


import com.payout.app.contracts.entity.Contract;
import com.payout.app.contracts.entity.ContractStatus;
import com.payout.app.contracts.repository.ContractRepository;
import com.payout.app.contracts.repository.ContractTemplateRepository;
import com.payout.app.iam.entity.User;
import com.payout.app.iam.entity.UserRole;
import com.payout.app.iam.repository.UserRepository;
import com.payout.app.tasks.dto.TaskCreateRequest;
import com.payout.app.tasks.dto.TaskResponse;
import com.payout.app.tasks.entity.Task;
import com.payout.app.tasks.entity.TaskStatus;
import com.payout.app.tasks.repository.TaskRepository;
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
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final ContractTemplateRepository contractTemplateRepository;

    @Transactional
    public TaskResponse create(User currentUser, TaskCreateRequest taskCreateRequest) {
        if (currentUser.getRole() == UserRole.CONTRACTOR) {
            throw new AccessDeniedException("You are not allowed to perform this action");
        }

        User assignedTo = null;
        if (taskCreateRequest.getAssignedToId() != null) {
            assignedTo = userRepository.findById(taskCreateRequest.getAssignedToId())
                    .filter(u -> u.getRole() == UserRole.CONTRACTOR)
                    .orElseThrow(() -> new EntityNotFoundException("Assigned to id not found"));
        }

        Task task = Task.builder()
                .company(currentUser.getCompany())
                .createdBy(currentUser)
                .assignedTo(assignedTo)
                .title(taskCreateRequest.getTitle())
                .description(taskCreateRequest.getDescription())
                .budget(taskCreateRequest.getBudget())
                .deadline(taskCreateRequest.getDeadline())
                .build();

        taskRepository.save(task);
        return toResponse(task);

    }


    public List<TaskResponse> listForUser(User user) {
        List<Task> tasks;
        if (user.getRole() == UserRole.CONTRACTOR) {
            tasks = taskRepository.findAvailableForContractor(user.getId());
        }else {
            tasks = taskRepository.findByCompanyId(user.getCompany().getId());
        }
        return tasks.stream().map(this::toResponse).toList();
    }

    public TaskResponse getById(User user, Long taskId) {
        Task task = findAccessibleTask(user, taskId);
        return toResponse(task);
    }


    @Transactional
    public TaskResponse accept(User user, Long taskId) {

        if (user.getRole() != UserRole.CONTRACTOR) {
            throw new AccessDeniedException("You are not allowed to perform this action");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        if (task.getStatus() != TaskStatus.CREATED) {
            throw new IllegalStateException("Cannot accept a task that has already been created");
        }

        if (task.getAssignedTo() != null && !task.getAssignedTo().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to perform this action");
        }

        // Create a contract only once, when the task has no linked contract yet.
        if (task.getContract() == null){
            var template = contractTemplateRepository.findByCompanyId(task.getCompany().getId())
                    .stream().findFirst().orElse(null);

            String contractNumber = generateContractNumber(task.getCompany().getId());

            Contract contract = Contract.builder()
                    .company(task.getCompany())
                    .template(template)
                    .contractor(user)
                    .contractNumber(contractNumber)
                    .subject(task.getTitle())
                    .amount(task.getBudget())
                    .status(ContractStatus.SIGNED)
                    .signedAt(OffsetDateTime.now())
                    .build();

            contractRepository.save(contract);
            task.setContract(contract);
        }

        task.setAssignedTo(user);
        task.setStatus(TaskStatus.ACCEPTED);
        taskRepository.save(task);

        return toResponse(task);
    }


    @Transactional
    public TaskResponse approve(User user, Long taskId) {
        if (user.getRole() != UserRole.CONTRACTOR) {
            throw new AccessDeniedException("You are not allowed to perform this action");
        }

        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getCompany().getId().equals(user.getCompany().getId()))
                .orElseThrow(() -> new  EntityNotFoundException("Task not found"));

        if (task.getStatus() != TaskStatus.REVIEW &&  task.getStatus() != TaskStatus.SUBMITTED) {            throw new IllegalStateException("Cannot approve task with status '" + task.getStatus() + "'");}

        task.setStatus(TaskStatus.APPROVED);
        taskRepository.save(task);
        return toResponse(task);

    }


    @Transactional
    public TaskResponse reject(User user, Long taskId) {
        if (user.getRole() != UserRole.CONTRACTOR) {
            throw new AccessDeniedException("You are not allowed to perform this action");
        }

        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getCompany().getId().equals(user.getCompany().getId()))
                .orElseThrow(() -> new  EntityNotFoundException("Task not found"));

        if (task.getStatus() != TaskStatus.REVIEW && task.getStatus() != TaskStatus.SUBMITTED) {
            throw new IllegalStateException("Cannot reject task with status '" + task.getStatus() + "'");
        }

        task.setStatus(TaskStatus.REJECTED);
        taskRepository.save(task);
        return toResponse(task);
    }

    private Task findAccessibleTask(User user, Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        if (user.getRole() == UserRole.CONTRACTOR) {
            boolean isAssigned = task.getAssignedTo() != null
                    && task.getAssignedTo().getId().equals(user.getId());
            boolean isOpenPool =  task.getAssignedTo() == null
                    && task.getStatus() ==  TaskStatus.CREATED;
            if (!isAssigned && !isOpenPool) {
                throw new AccessDeniedException("You are not allowed to perform this action");
            }
        } else {
            if (!task.getCompany().getId().equals(user.getCompany().getId())) {
                throw new AccessDeniedException("You are not allowed to perform this action");
            }
        }

        return task;

    }

    private String generateContractNumber(long companyId){
        int year = Year.now().getValue();
        long count = contractRepository.countByCompanyId(companyId);
        return String.format("CMP%d-%d-%04d", companyId, year, count + 1);
    }


    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .companyId(task.getCompany().getId())
                .createdById(task.getCreatedBy().getId())
                .assignedToId(task.getAssignedTo() != null ? task.getAssignedTo().getId() : null)
                .contractId(task.getContract() != null ? task.getContract().getId() : null)
                .title(task.getTitle())
                .description(task.getDescription())
                .budget(task.getBudget())
                .deadline(task.getDeadline())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .build();
    }

}
