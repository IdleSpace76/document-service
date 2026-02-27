package ru.test.document_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.test.document_service.domain.DocumentItem;
import ru.test.document_service.dto.*;
import ru.test.document_service.dto.batch.BatchDocumentApproveRequest;
import ru.test.document_service.dto.batch.BatchDocumentSubmitRequest;
import ru.test.document_service.dto.batch.DocumentBatchResponse;
import ru.test.document_service.mapper.DocumentMapper;
import ru.test.document_service.service.DocumentService;
import ru.test.document_service.service.BatchDocumentService;

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
    private final BatchDocumentService batchDocumentService;

    /**
     * Создать документ
     */
    @PostMapping
    public ResponseEntity<DocumentResponse> create(@Valid @RequestBody DocumentCreateRequest request) {
        DocumentItem document = documentService.createDocument(request.getAuthor(), request.getTitle());
        DocumentResponse body = DocumentMapper.toResponse(document);

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
        return DocumentMapper.toResponse(document);
    }

    /**
     * Получить все документы
     */
    @GetMapping
    public List<DocumentResponse> getAll() {
        return documentService.getAll().stream()
                .map(DocumentMapper::toResponse)
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
        return DocumentMapper.toResponse(document);
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
        return DocumentMapper.toResponse(document);
    }

    /**
     * Пакетный submit документов
     */
    @PostMapping("/batch/submit")
    public DocumentBatchResponse submitBatch(@Valid @RequestBody BatchDocumentSubmitRequest request) {
        return batchDocumentService.submitBatch(
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
        return batchDocumentService.approveBatch(
                request.getDocumentIds(),
                request.getApprover(),
                request.getComment()
        );
    }

    @GetMapping("/search")
    public List<DocumentResponse> search(@ModelAttribute DocumentSearchFilter filter) {
        return documentService.search(filter).stream()
                .map(DocumentMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}/details")
    public DocumentDetailsResponse getDocumentWithHistory(@PathVariable Long id) {
        return documentService.getWithHistory(id);
    }

    /**
     * Пакетное получение документов по списку id
     * Пример:
     * GET /api/documents/batch?ids=1,2,3&page=0&size=10&sort=createdAt,desc
     */
    @GetMapping("/batch")
    public Page<DocumentResponse> getDocumentsBatch(
            @RequestParam List<Long> ids,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

        return documentService.getByIds(ids, pageable);
    }
}
