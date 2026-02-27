package ru.test.document_service.dto.batch;

import lombok.Builder;
import lombok.Getter;

/**
 * Результат обработки одного документа в пакетной операции
 *
 * @author a.zharov
 */
@Getter
@Builder
public class DocumentBatchItemResult {

    private Long documentId;
    private boolean success;
    private String message;
}
