package com.payout.app.contracts.entity;


import com.payout.app.iam.entity.Company;
import com.payout.app.iam.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private ContractTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id", nullable = false)
    private User contractor;

    @Column(name = "contract_number", nullable = false, unique = true)
    private String contractNumber;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String subject;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "contract_status")
    @Builder.Default
    private ContractStatus status =  ContractStatus.DRAFT;

    @Column(name = "signed_at")
    private OffsetDateTime signedAt;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected  void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

}
