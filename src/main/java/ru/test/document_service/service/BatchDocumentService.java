package ru.test.document_service.service;

import ru.test.document_service.dto.batch.DocumentBatchResponse;

import java.util.List;

/**
 * @author a.zharov
 */
public interface BatchDocumentService {

    /**
     * Пакетный submit документов
     */
    DocumentBatchResponse submitBatch(List<Long> documentIds, String performedBy, String comment);

    /**
     * Пакетный approve документов
     */
    DocumentBatchResponse approveBatch(List<Long> documentIds, String approver, String comment);
}
