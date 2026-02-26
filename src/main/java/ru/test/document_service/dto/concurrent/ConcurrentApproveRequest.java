package ru.test.document_service.dto.concurrent;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Запрос на проверку конкурентного апрува
 *
 * @author a.zharov
 */
@Data
public class ConcurrentApproveRequest {

    /**
     * id документа
     */
    @NotNull
    private Long documentId;

    /**
     * Количество потоков.
     */
    @Min(1)
    private int threads;

    /**
     * Количество попыток апрува на каждый поток.
     * Итого = threads * attempts
     */
    @Min(1)
    private int attemptsPerThread;

    /**
     * Автор апрува
     */
    @NotNull
    private String approver;

    /**
     * Комментарий
     */
    private String comment;
}
