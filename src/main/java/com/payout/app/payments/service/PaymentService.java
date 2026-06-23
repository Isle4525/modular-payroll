//package com.payout.app.payments.service;
//
//
//import com.payout.app.iam.entity.User;
//import com.payout.app.iam.entity.UserRole;
//import com.payout.app.payments.dto.PaymentResponse;
//import com.payout.app.payments.entity.Payment;
//import com.payout.app.payments.repository.PaymentRepository;
//import com.payout.app.tasks.entity.Task;
//import com.payout.app.tasks.entity.TaskStatus;
//import com.payout.app.tasks.repository.TaskRepository;
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class PaymentService {
//
//    private final PaymentRepository paymentRepository;
//    private final TaskRepository taskRepository;
//
//
//    @Transactional
//    public PaymentResponse createPayment(User user, Long taskId) {
//        if (user.getRole() == UserRole.CONTRACTOR) {
//            throw new AccessDeniedException("Contractors cannot initiate payments");
//        }
//
//        Task task = taskRepository.findById(taskId)
//                .filter(t -> t.getCompany().getId().equals(user.getCompany().getId()))
//                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
//
//
//        if (task.getStatus() != TaskStatus.APPROVED) {
//            throw new IllegalStateException("Payment can only be initiated for APPROVED tasks");
//        }
//
//        if (task.getContract() == null){
//            throw new IllegalStateException("Task has no contract — cannot initiate payment");
//        }
//
//        // мок платёжного провайдера — на MVP просто генерируем tx_id
//        // позже здесь будет вызов реального банковского API (Kaspi, Jusan и т.д.)
//
//
//    }
//
//
//    private PaymentResponse toResponse(Payment payment) {
//        return PaymentResponse.builder()
//                .id(payment.getId())
//                .taskId(payment.getTask().getId())
//                .contractId(payment.getContract().getId())
//                .contractorId(payment.getContractor().getId())
//                .amount(payment.getAmount())
//                .status(payment.getStatus())
//                .providerTxId(payment.getProviderTxId())
//                .createdAt(payment.getCreatedAt())
//                .paidAt(payment.getPaidAt())
//                .build();
//    }
//
//
//}
