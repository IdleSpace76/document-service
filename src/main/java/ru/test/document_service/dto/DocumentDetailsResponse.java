package ru.test.document_service.dto;

import lombok.Builder;
import lombok.Getter;
import ru.test.document_service.domain.enums.DocumentStatus;

import java.time.Instant;
import java.util.List;

/**
 * @author a.zharov
 */
@Getter
@Builder
public class DocumentDetailsResponse {

    private DocumentResponse document;
    private List<DocumentHistoryItemResponse> history;
}
