package ru.test.document_service.dto.concurrent;

import lombok.Builder;
import lombok.Getter;
import ru.test.document_service.domain.enums.DocumentStatus;

/**
 * Результат проверки конкурентного апрува
 *
 * @author a.zharov
 */
@Getter
@Builder
public class ConcurrentApproveResponse {

    private int threads;
    private int attemptsPerThread;
    private int totalAttempts;
    private int successCount;
    private int conflictCount;
    private int errorCount;
    private DocumentStatus finalStatus;
    private long registryRecordsCount;
}
