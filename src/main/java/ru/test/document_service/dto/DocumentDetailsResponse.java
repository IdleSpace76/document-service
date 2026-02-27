package ru.test.document_service.dto;

import lombok.Builder;
import lombok.Getter;

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
