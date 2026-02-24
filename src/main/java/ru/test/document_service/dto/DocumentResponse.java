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

    Long id;
    String uid;
    String author;
    String title;
    DocumentStatus status;
    Instant createdAt;
    Instant updatedAt;
}
