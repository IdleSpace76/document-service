package ru.test.document_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * @author a.zharov
 */
@Entity
@Table(name = "approval_registry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRegistryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approval_registry_seq")
    @SequenceGenerator(name = "approval_registry_seq", sequenceName = "approval_registry_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    /**
     * Документ, к которому относится запись
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentItem document;

    /**
     * Автор утверждения
     */
    @Column(name = "approver", nullable = false)
    private String approver;

    /**
     * Дата создания
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;
}
