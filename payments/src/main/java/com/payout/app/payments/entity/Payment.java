package com.payout.app.payments.entity;


import com.payout.app.contracts.entity.Contract;
import com.payout.app.iam.entity.User;
import com.payout.app.tasks.entity.Task;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id",  nullable = false)
    private Task task;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id",  nullable = false)
    private User contractor;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "payment_status")
    @Builder.Default
    private PaymentStatus status =  PaymentStatus.PENDING;

    @Column(name = "provider_tx_id")
    private String providerTxId;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @PrePersist
    public void onCreate() {
        createdAt = OffsetDateTime.now();
    }

}
