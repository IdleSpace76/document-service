package ru.test.document_service.dto;

import lombok.Builder;
import lombok.Getter;
import ru.test.document_service.domain.enums.DocumentStatus;

import java.time.Instant;

/**
 * DTO для отдачи документа наружу
 *
 * @author a.zharov
 */
@Getter
@Builder
public class DocumentResponse {

    private Long id;
    private String uid;
    private String author;
    private String title;
    private DocumentStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
