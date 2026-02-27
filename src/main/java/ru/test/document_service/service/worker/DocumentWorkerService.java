package ru.test.document_service.service.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.test.document_service.dao.DocumentItemRepository;
import ru.test.document_service.domain.DocumentItem;
import ru.test.document_service.domain.enums.DocumentStatus;
import ru.test.document_service.dto.batch.DocumentBatchItemResult;
import ru.test.document_service.dto.batch.DocumentBatchResponse;
import ru.test.document_service.dto.worker.WorkerRunResponse;
import ru.test.document_service.service.BatchDocumentService;

import java.util.List;

/**
 * @author a.zharov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentWorkerService {

    private final DocumentItemRepository documentRepository;
    private final BatchDocumentService batchDocumentService;

    /**
     * Воркер: отправить на субмит batchSize документов в статусе DRAFT
     */
    public WorkerRunResponse runSubmitWorker(int batchSize) {
        List<DocumentItem> draftDocs = documentRepository
                .findByStatus(DocumentStatus.DRAFT, PageRequest.of(0, batchSize, Sort.by("id").ascending()))
                .getContent();

        if (draftDocs.isEmpty()) {
            return WorkerRunResponse.builder()
                    .requestedBatchSize(batchSize)
                    .selectedCount(0)
                    .successCount(0)
                    .failedCount(0)
                    .build();
        }

        DocumentBatchResponse response = batchDocumentService.submitBatch(
                draftDocs.stream().map(DocumentItem::getId).toList(),
                "submit-worker",
                "Субмит от submit-worker");

        int success = 0;
        int failed = 0;
        for (DocumentBatchItemResult item : response.getResults()) {
            if (item.isSuccess()) {
                success++;
            }
            else {
                failed++;
                log.warn(
                        "submit-worker: ошибка субмита на документе id={} : reason={}",
                        item.getDocumentId(),
                        item.getMessage()
                );
            }
        }

        return WorkerRunResponse.builder()
                .requestedBatchSize(batchSize)
                .selectedCount(draftDocs.size())
                .successCount(success)
                .failedCount(failed)
                .build();
    }

    /**
     * Воркер: отправить на апрув batchSize документов в статусе SUBMITTED
     */
    public WorkerRunResponse runApproveWorker(int batchSize) {
        List<DocumentItem> submittedDocs = documentRepository
                .findByStatus(DocumentStatus.SUBMITTED, PageRequest.of(0, batchSize, Sort.by("id").ascending()))
                .getContent();

        if (submittedDocs.isEmpty()) {
            return WorkerRunResponse.builder()
                    .requestedBatchSize(batchSize)
                    .selectedCount(0)
                    .successCount(0)
                    .failedCount(0)
                    .build();
        }

        DocumentBatchResponse response = batchDocumentService.approveBatch(
                submittedDocs.stream().map(DocumentItem::getId).toList(),
                "approve-worker",
                "Апрув от approve-worker");

        int success = 0;
        int failed = 0;
        for (DocumentBatchItemResult item : response.getResults()) {
            if (item.isSuccess()) {
                success++;
            }
            else {
                failed++;
                log.warn(
                        "approve-worker: ошибка апрува на документе id={} : reason={}",
                        item.getDocumentId(),
                        item.getMessage()
                );
            }
        }

        return WorkerRunResponse.builder()
                .requestedBatchSize(batchSize)
                .selectedCount(submittedDocs.size())
                .successCount(success)
                .failedCount(failed)
                .build();
    }
}
