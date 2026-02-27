package ru.test.document_service.service.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
        long started = System.nanoTime();

        Page<DocumentItem> page = documentRepository.findByStatus(
                DocumentStatus.DRAFT,
                PageRequest.of(0, batchSize, Sort.by("id").ascending())
        );

        List<DocumentItem> draftDocs = page.getContent();

        if (draftDocs.isEmpty()) {
            log.debug("submit-worker: нет документов в статусе DRAFT для обработки");
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
                "Субмит от submit-worker"
        );

        int success = 0;
        int failed = 0;
        for (DocumentBatchItemResult item : response.getResults()) {
            if (item.isSuccess()) {
                success++;
            } else {
                failed++;
                log.warn(
                        "submit-worker: ошибка субмита документа id={} : reason={}",
                        item.getDocumentId(),
                        item.getMessage()
                );
            }
        }

        long durationMs = (System.nanoTime() - started) / 1_000_000;

        long totalBefore = page.getTotalElements();
        long remainingDraft = Math.max(0, totalBefore - success);

        log.info(
                "submit-worker: batchSize={}, selected={}, success={}, failed={}, remainingDraft~={}, time={} ms",
                batchSize, draftDocs.size(), success, failed, remainingDraft, durationMs
        );

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
        long started = System.nanoTime();

        Page<DocumentItem> page = documentRepository.findByStatus(
                DocumentStatus.SUBMITTED,
                PageRequest.of(0, batchSize, Sort.by("id").ascending())
        );

        List<DocumentItem> submittedDocs = page.getContent();

        if (submittedDocs.isEmpty()) {
            log.debug("approve-worker: нет документов в статусе SUBMITTED для обработки");
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
                "Апрув от approve-worker"
        );

        int success = 0;
        int failed = 0;
        for (DocumentBatchItemResult item : response.getResults()) {
            if (item.isSuccess()) {
                success++;
            } else {
                failed++;
                log.warn(
                        "approve-worker: ошибка апрува документа id={} : reason={}",
                        item.getDocumentId(),
                        item.getMessage()
                );
            }
        }

        long durationMs = (System.nanoTime() - started) / 1_000_000;

        long totalBefore = page.getTotalElements();
        long remainingSubmitted = Math.max(0, totalBefore - success);

        log.info(
                "approve-worker: batchSize={}, selected={}, success={}, failed={}, remainingSubmitted~={}, time={} ms",
                batchSize, submittedDocs.size(), success, failed, remainingSubmitted, durationMs
        );

        return WorkerRunResponse.builder()
                .requestedBatchSize(batchSize)
                .selectedCount(submittedDocs.size())
                .successCount(success)
                .failedCount(failed)
                .build();
    }
}
