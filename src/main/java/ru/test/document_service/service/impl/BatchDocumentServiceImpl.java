package ru.test.document_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class BatchDocumentServiceImpl implements BatchDocumentService {

    private final DocumentService documentService;

    @Override
    public DocumentBatchResponse submitBatch(List<Long> documentIds, String performedBy, String comment) {
        long started = System.nanoTime();

        List<DocumentBatchItemResult> results = new ArrayList<>();

        int success = 0;
        int failed = 0;

        for (Long id : documentIds) {
            try {
                documentService.submit(id, performedBy, comment);
                results.add(DocumentBatchItemResult.builder()
                        .documentId(id)
                        .success(true)
                        .message(null)
                        .build());
                success++;
            }
            catch (Exception ex) {
                results.add(DocumentBatchItemResult.builder()
                        .documentId(id)
                        .success(false)
                        .message(ex.getMessage())
                        .build());
                failed++;
            }
        }

        long durationMs = (System.nanoTime() - started) / 1_000_000;

        log.info(
                "Batch SUBMIT: size={}, success={}, failed={}, performedBy={}, time={} ms",
                documentIds.size(), success, failed, performedBy, durationMs
        );

        return DocumentBatchResponse.builder()
                .results(results)
                .build();
    }

    @Override
    public DocumentBatchResponse approveBatch(List<Long> documentIds, String approver, String comment) {
        long started = System.nanoTime();
        List<DocumentBatchItemResult> results = new ArrayList<>();

        int success = 0;
        int failed = 0;
        for (Long id : documentIds) {
            try {
                documentService.approve(id, approver, comment);
                results.add(DocumentBatchItemResult.builder()
                        .documentId(id)
                        .success(true)
                        .message(null)
                        .build());
                success++;
            }
            catch (Exception ex) {
                results.add(DocumentBatchItemResult.builder()
                        .documentId(id)
                        .success(false)
                        .message(ex.getMessage())
                        .build());
                failed++;
            }
        }

        long durationMs = (System.nanoTime() - started) / 1_000_000;

        log.info(
                "Batch APPROVE: size={}, success={}, failed={}, approver={}, time={} ms",
                documentIds.size(), success, failed, approver, durationMs
        );

        return DocumentBatchResponse.builder()
                .results(results)
                .build();
    }
}
