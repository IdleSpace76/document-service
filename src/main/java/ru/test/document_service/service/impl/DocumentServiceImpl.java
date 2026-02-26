package ru.test.document_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.test.document_service.dao.ApprovalRegistryRecordRepository;
import ru.test.document_service.dao.DocumentHistoryRepository;
import ru.test.document_service.dao.DocumentItemRepository;
import ru.test.document_service.domain.ApprovalRegistryRecord;
import ru.test.document_service.domain.DocumentHistory;
import ru.test.document_service.domain.DocumentItem;
import ru.test.document_service.domain.enums.DocumentAction;
import ru.test.document_service.domain.enums.DocumentStatus;
import ru.test.document_service.dto.DocumentSearchFilter;
import ru.test.document_service.dto.batch.DocumentBatchItemResult;
import ru.test.document_service.dto.batch.DocumentBatchResponse;
import ru.test.document_service.service.DocumentService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentItemRepository documentRepository;
    private final DocumentHistoryRepository historyRepository;
    private final ApprovalRegistryRecordRepository approvalRegistryRepository;

    @Override
    @Transactional
    public DocumentItem createDocument(String author, String title) {
        String uid = generateUniqueUid();

        DocumentItem document = new DocumentItem();
        document.setUid(uid);
        document.setAuthor(author);
        document.setTitle(title);
        return documentRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentItem getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Документ по id не найден, id = " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentItem> getAll() {
        return documentRepository.findAll();
    }

    @Override
    @Transactional
    public DocumentItem submit(Long id, String performedBy, String comment) {
        DocumentItem document = getById(id);

        if (document.getStatus() != DocumentStatus.DRAFT) {
            throw new IllegalStateException(
                    "Невозможно утвердить, документ в статусе " + document.getStatus()
            );
        }

        document.setStatus(DocumentStatus.SUBMITTED);
        DocumentItem saved = documentRepository.save(document);

        // запись в историю
        DocumentHistory history = new DocumentHistory();
        history.setDocument(saved);
        history.setPerformedBy(performedBy);
        history.setAction(DocumentAction.SUBMIT);
        history.setComment(comment);
        historyRepository.save(history);

        return saved;
    }

    @Override
    @Transactional
    public DocumentItem approve(Long id, String approver, String comment) {
        DocumentItem document = documentRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new NoSuchElementException("Документ по id не найден, id = " + id));

        if (document.getStatus() != DocumentStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "Невозможно утвердить, документ в статусе " + document.getStatus()
            );
        }

        document.setStatus(DocumentStatus.APPROVED);
        DocumentItem saved = documentRepository.save(document);

        // запись в историю
        DocumentHistory history = new DocumentHistory();
        history.setDocument(saved);
        history.setPerformedBy(approver);
        history.setAction(DocumentAction.APPROVE);
        history.setComment(comment);
        historyRepository.save(history);

        // запись в реестр
        ApprovalRegistryRecord registryRecord = new ApprovalRegistryRecord();
        registryRecord.setDocument(saved);
        registryRecord.setApprover(approver);
        approvalRegistryRepository.save(registryRecord);

        return saved;
    }

    @Override
    @Transactional
    public DocumentBatchResponse submitBatch(List<Long> documentIds, String performedBy, String comment) {
        List<DocumentBatchItemResult> results = new ArrayList<>();

        for (Long id : documentIds) {
            try {
                submit(id, performedBy, comment);
                results.add(DocumentBatchItemResult.builder()
                        .documentId(id)
                        .success(true)
                        .message(null)
                        .build());
            }
            catch (Exception ex) {
                results.add(DocumentBatchItemResult.builder()
                        .documentId(id)
                        .success(false)
                        .message(ex.getMessage())
                        .build());
            }
        }

        return DocumentBatchResponse.builder()
                .results(results)
                .build();
    }

    @Override
    @Transactional
    public DocumentBatchResponse approveBatch(List<Long> documentIds, String approver, String comment) {
        List<DocumentBatchItemResult> results = new ArrayList<>();

        for (Long id : documentIds) {
            try {
                approve(id, approver, comment);
                results.add(DocumentBatchItemResult.builder()
                        .documentId(id)
                        .success(true)
                        .message(null)
                        .build());
            }
            catch (Exception ex) {
                results.add(DocumentBatchItemResult.builder()
                        .documentId(id)
                        .success(false)
                        .message(ex.getMessage())
                        .build());
            }
        }

        return DocumentBatchResponse.builder()
                .results(results)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentItem> search(DocumentSearchFilter filter) {
        Specification<DocumentItem> spec = Specification.unrestricted();

        if (filter.getStatus() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), filter.getStatus()));
        }
        if (filter.getAuthor() != null && !filter.getAuthor().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("author"), filter.getAuthor()));
        }
        if (filter.getCreatedFrom() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedFrom()));
        }
        if (filter.getCreatedTo() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedTo()));
        }

        return documentRepository.findAll(spec);
    }

    /**
     * Генерация УИД
     */
    private String generateUniqueUid() {
        return UUID.randomUUID().toString();
    }
}
