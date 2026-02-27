package ru.test.document_service.dto.worker;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author a.zharov
 */
@Getter
@ToString
@Builder
public class WorkerRunResponse {

    private int requestedBatchSize;
    private int selectedCount;
    private int successCount;
    private int failedCount;
}
