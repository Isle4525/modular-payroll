package com.payout.app.submissions.service;

import com.payout.app.iam.entity.User;
import com.payout.app.iam.entity.UserRole;
import com.payout.app.submissions.dto.SubmissionRequest;
import com.payout.app.submissions.dto.SubmissionResponse;
import com.payout.app.submissions.entity.Submission;
import com.payout.app.submissions.repository.SubmissionRepository;
import com.payout.app.tasks.entity.Task;
import com.payout.app.tasks.entity.TaskStatus;
import com.payout.app.tasks.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;


    @Transactional
    public SubmissionResponse submit(User user, Long taskId, SubmissionRequest request) {

        if(user.getRole() != UserRole.CONTRACTOR) {
            throw new AccessDeniedException("User is not a contractor");
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        if (!task.getAssignedTo().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to perform this action");
        }

        if(task.getStatus() != TaskStatus.ACCEPTED &&  task.getStatus() != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot submit work for task with status '" + task.getStatus() + "'");
        }

        Submission submission = Submission.builder()
                .task(task)
                .contractor(user)
                .content(request.getContent())
                .attachments(request.getAttachments())
                .build();

        submissionRepository.save(submission);

        task.setStatus(TaskStatus.SUBMITTED);

        taskRepository.save(task);

        return toResponse(submission);
    }



    public List<SubmissionResponse> listForTask(User user, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));


        // contractor only see own submission
        if (user.getRole() ==  UserRole.CONTRACTOR) {
            if (!task.getAssignedTo().getId().equals(user.getCompany().getId())) {
                throw new AccessDeniedException("You are not allowed to perform this action");
            }
        } else {
            if (!task.getCompany().getId().equals(user.getCompany().getId())) {
                throw new AccessDeniedException("You are not allowed to perform this action");
            }
        }

        return submissionRepository.findByTaskId(taskId)
                .stream()
                .map(this::toResponse)
                .toList();

    }









    private SubmissionResponse toResponse(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .taskId(submission.getTask().getId())
                .contractorId(submission.getContractor().getId())
                .content(submission.getContent())
                .attachments(submission.getAttachments())
                .status(submission.getStatus())
                .submittedAt(submission.getSubmittedAt())
                .build();
    }
}
