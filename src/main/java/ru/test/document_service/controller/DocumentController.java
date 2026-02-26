package ru.test.document_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.test.document_service.domain.DocumentItem;
import ru.test.document_service.dto.*;
import ru.test.document_service.dto.batch.BatchDocumentApproveRequest;
import ru.test.document_service.dto.batch.BatchDocumentSubmitRequest;
import ru.test.document_service.dto.batch.DocumentBatchResponse;
import ru.test.document_service.service.DocumentService;

import java.net.URI;
import java.util.List;

/**
 * @author a.zharov
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Создать документ
     */
    @PostMapping
    public ResponseEntity<DocumentResponse> create(@Valid @RequestBody DocumentCreateRequest request) {
        DocumentItem document = documentService.createDocument(request.getAuthor(), request.getTitle());
        DocumentResponse body = createResponseBody(document);

        return ResponseEntity
                .created(URI.create("/api/documents/" + document.getId()))
                .body(body);
    }

    /**
     * Получить документ по id
     */
    @GetMapping("/{id}")
    public DocumentResponse getById(@PathVariable Long id) {
        DocumentItem document = documentService.getById(id);
        return createResponseBody(document);
    }

    /**
     * Получить все документы
     */
    @GetMapping
    public List<DocumentResponse> getAll() {
        return documentService.getAll().stream()
                .map(this::createResponseBody)
                .toList();
    }

    /**
     * Отправить документ на утверждение (SUBMIT)
     */
    @PostMapping("/{id}/submit")
    public DocumentResponse submit(
            @PathVariable Long id,
            @Valid @RequestBody DocumentSubmitRequest request
    ) {
        DocumentItem document = documentService.submit(id, request.getPerformedBy(), request.getComment());
        return createResponseBody(document);
    }

    /**
     * Утвердить документ (APPROVE)
     */
    @PostMapping("/{id}/approve")
    public DocumentResponse approve(
            @PathVariable Long id,
            @Valid @RequestBody DocumentApproveRequest request
    ) {
        DocumentItem document = documentService.approve(id, request.getApprover(), request.getComment());
        return createResponseBody(document);
    }

    /**
     * Пакетный submit документов
     */
    @PostMapping("/batch/submit")
    public DocumentBatchResponse submitBatch(@Valid @RequestBody BatchDocumentSubmitRequest request) {
        return documentService.submitBatch(
                request.getDocumentIds(),
                request.getPerformedBy(),
                request.getComment()
        );
    }

    /**
     * Пакетный approve документов
     */
    @PostMapping("/batch/approve")
    public DocumentBatchResponse approveBatch(@Valid @RequestBody BatchDocumentApproveRequest request) {
        return documentService.approveBatch(
                request.getDocumentIds(),
                request.getApprover(),
                request.getComment()
        );
    }

    @GetMapping("/search")
    public List<DocumentResponse> search(@ModelAttribute DocumentSearchFilter filter) {
        return documentService.search(filter).stream()
                .map(this::createResponseBody)
                .toList();
    }

    private DocumentResponse createResponseBody(DocumentItem document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .uid(document.getUid())
                .author(document.getAuthor())
                .title(document.getTitle())
                .status(document.getStatus())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
