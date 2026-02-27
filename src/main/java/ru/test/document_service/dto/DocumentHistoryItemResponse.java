package ru.test.document_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import ru.test.document_service.domain.enums.DocumentAction;

import java.time.Instant;

/**
 * @author a.zharov
 */
@Getter
@Builder
public class DocumentHistoryItemResponse {

    private Long id;
    private DocumentAction action;
    private String performedBy;
    private String comment;
    private Instant createdAt;
}
