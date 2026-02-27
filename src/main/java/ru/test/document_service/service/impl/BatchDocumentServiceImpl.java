package ru.test.document_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.test.document_service.dto.batch.DocumentBatchItemResult;
import ru.test.document_service.dto.batch.DocumentBatchResponse;
import ru.test.document_service.service.DocumentService;
import ru.test.document_service.service.BatchDocumentService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
public class BatchDocumentServiceImpl implements BatchDocumentService {

    private final DocumentService documentService;

    @Override
    public DocumentBatchResponse submitBatch(List<Long> documentIds, String performedBy, String comment) {
        List<DocumentBatchItemResult> results = new ArrayList<>();

        for (Long id : documentIds) {
            try {
                documentService.submit(id, performedBy, comment);
                results.add(DocumentBatchItemResult.builder()
                        .documentId(id)
                        .success(true)
                        .message(null)
                        .build());
            }
            catch (Exception ex) {
                results.add(DocumentBatchItemResult.builder()
                        .documentId(id)
                        .success(false)
                        .message(ex.getMessage())
                        .build());
            }
        }

        return DocumentBatchResponse.builder()
                .results(results)
                .build();
    }

    @Override
    public DocumentBatchResponse approveBatch(List<Long> documentIds, String approver, String comment) {
        List<DocumentBatchItemResult> results = new ArrayList<>();

        for (Long id : documentIds) {
            try {
                documentService.approve(id, approver, comment);
                results.add(DocumentBatchItemResult.builder()
                        .documentId(id)
                        .success(true)
                        .message(null)
                        .build());
            }
            catch (Exception ex) {
                results.add(DocumentBatchItemResult.builder()
                        .documentId(id)
                        .success(false)
                        .message(ex.getMessage())
                        .build());
            }
        }

        return DocumentBatchResponse.builder()
                .results(results)
                .build();
    }
}
