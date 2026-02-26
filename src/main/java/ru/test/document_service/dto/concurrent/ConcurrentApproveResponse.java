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

    int threads;
    int attemptsPerThread;
    int totalAttempts;

    int successCount;
    int conflictCount;
    int errorCount;

    DocumentStatus finalStatus;
    long registryRecordsCount;
}
