package ru.test.document_service.dto.batch;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Ответ на пакетный субмит / апрув
 *
 * @author a.zharov
 */
@Getter
@Builder
public class DocumentBatchResponse {

    /**
     * Список результатов по каждому документу
     */
    List<DocumentBatchItemResult> results;
}
