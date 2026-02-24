package ru.test.document_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.test.document_service.domain.enums.DocumentStatus;

import java.time.Instant;

/**
 * @author a.zharov
 */
@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentItem {

    /**
     * Внутренний id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documents_seq")
    @SequenceGenerator(name = "documents_seq", sequenceName = "documents_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    /**
     * УИД
     */
    @Column(name = "uid", nullable = false, unique = true)
    private String uid;

    /**
     * Автор
     */
    @Column(name = "author", nullable = false)
    private String author;

    /**
     * Название документа
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Статус
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DocumentStatus status;

    /**
     * Дата создания
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * Дата обновления
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (this.status == null) {
            this.status = DocumentStatus.DRAFT;
        }
    }
}
