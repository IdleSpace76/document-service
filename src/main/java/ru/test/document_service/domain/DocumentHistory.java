package ru.test.document_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.test.document_service.domain.enums.DocumentAction;

import java.time.Instant;

/**
 * @author a.zharov
 */
@Entity
@Table(name = "document_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_history_seq")
    @SequenceGenerator(name = "document_history_seq", sequenceName = "document_history_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    /**
     * Документ, к которому относится запись
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private DocumentItem document;

    /**
     * Автор действия
     */
    @Column(name = "performed_by", nullable = false)
    private String performedBy;

    /**
     * Дата создания
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * Тип действия
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private DocumentAction action;

    /**
     * Комментарий
     */
    @Column(name = "comment")
    private String comment;
}
