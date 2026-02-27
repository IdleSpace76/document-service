package ru.test.document_service.service.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.test.document_service.dto.worker.WorkerRunResponse;

/**
 * @author a.zharov
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentWorkersScheduler {

    private final DocumentWorkerService documentWorkerService;

    @Value("${workers.submit.enabled}")
    private boolean submitEnabled;

    @Value("${workers.submit.batch-size}")
    private int submitBatchSize;

    @Value("${workers.approve.enabled}")
    private boolean approveEnabled;

    @Value("${workers.approve.batch-size}")
    private int approveBatchSize;

    /**
     * Периодически отправляет документы из DRAFT в SUBMITTED
     */
    @Scheduled(fixedDelayString = "${workers.submit.delay-ms}")
    public void runSubmitWorkerScheduled() {
        if (!submitEnabled) {
            return;
        }
        WorkerRunResponse result = documentWorkerService.runSubmitWorker(submitBatchSize);
        log.debug("Субмит воркером: {}", result.toString());
    }

    /**
     * Периодически отправляет документы из SUBMITTED в APPROVED
     */
    @Scheduled(fixedDelayString = "${workers.approve.delay-ms}")
    public void runApproveWorkerScheduled() {
        if (!approveEnabled) {
            return;
        }
        WorkerRunResponse result = documentWorkerService.runApproveWorker(approveBatchSize);
        log.debug("Апрув воркером: {}", result.toString());
    }
}
