package com.payout.app.payments.entity;


import com.payout.app.contracts.entity.Contract;
import com.payout.app.iam.entity.User;
import com.payout.app.tasks.entity.Task;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Task task;

    private Contract contract;

    private User contractor;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String providerTxId;

    private OffsetDateTime createdAt;

    private OffsetDateTime paidAt;

    @PrePersist
    public void onCreate() {
        createdAt = OffsetDateTime.now();
    }

}
