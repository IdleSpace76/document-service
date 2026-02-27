package ru.test.document_service.mapper;

import ru.test.document_service.domain.DocumentHistory;
import ru.test.document_service.domain.DocumentItem;
import ru.test.document_service.dto.DocumentDetailsResponse;
import ru.test.document_service.dto.DocumentHistoryItemResponse;
import ru.test.document_service.dto.DocumentResponse;

import java.util.List;

/**
 * @author a.zharov
 */
public class DocumentMapper {

    public static DocumentResponse toResponse(DocumentItem document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .uid(document.getUid())
                .author(document.getAuthor())
                .title(document.getTitle())
                .status(document.getStatus())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    public static DocumentDetailsResponse toDetails(DocumentItem document,
                                             List<DocumentHistory> history) {
        List<DocumentHistoryItemResponse> historyDtos = history.stream()
                .map(h -> DocumentHistoryItemResponse.builder()
                        .id(h.getId())
                        .action(h.getAction())
                        .performedBy(h.getPerformedBy())
                        .comment(h.getComment())
                        .createdAt(h.getCreatedAt())
                        .build())
                .toList();

        return DocumentDetailsResponse.builder()
                .document(toResponse(document))
                .history(historyDtos)
                .build();
    }
}
