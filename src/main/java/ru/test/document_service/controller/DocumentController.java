package ru.test.document_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.test.document_service.domain.DocumentItem;
import ru.test.document_service.dto.DocumentCreateRequest;
import ru.test.document_service.dto.DocumentResponse;
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
