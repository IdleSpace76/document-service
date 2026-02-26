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

    Long documentId;
    boolean success;
    String message;
}
