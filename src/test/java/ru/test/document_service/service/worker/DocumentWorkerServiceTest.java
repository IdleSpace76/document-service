package ru.test.document_service.service.worker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.test.document_service.dao.DocumentItemRepository;
import ru.test.document_service.domain.DocumentItem;
import ru.test.document_service.domain.enums.DocumentStatus;
import ru.test.document_service.dto.batch.DocumentBatchItemResult;
import ru.test.document_service.dto.batch.DocumentBatchResponse;
import ru.test.document_service.dto.worker.WorkerRunResponse;
import ru.test.document_service.service.BatchDocumentService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author a.zharov
 */
@ExtendWith(MockitoExtension.class)
public class DocumentWorkerServiceTest {

    @Mock
    private DocumentItemRepository documentRepository;

    @Mock
    private BatchDocumentService batchDocumentService;

    @InjectMocks
    private DocumentWorkerService documentWorkerService;

    /**
     * happy-path по одному документу (submit)
     */
    @Test
    void runSubmitWorker_happyPathSingleDocument() {
        int batchSize = 1;

        // given: один документ в DRAFT
        DocumentItem draft = new DocumentItem();
        draft.setId(1L);
        draft.setStatus(DocumentStatus.DRAFT);

        Page<DocumentItem> page = new PageImpl<>(
                List.of(draft),
                PageRequest.of(0, batchSize),
                1
        );

        when(documentRepository.findByStatus(eq(DocumentStatus.DRAFT), any(Pageable.class)))
                .thenReturn(page);

        // ответ от batchDocumentService: один успешный результат
        DocumentBatchItemResult okResult = DocumentBatchItemResult.builder()
                .documentId(draft.getId())
                .success(true)
                .message(null)
                .build();
        DocumentBatchResponse response = DocumentBatchResponse.builder().results(List.of(okResult)).build();

        when(batchDocumentService.submitBatch(
                eq(List.of(draft.getId())),
                anyString(),
                anyString()
        )).thenReturn(response);

        // when
        WorkerRunResponse workerResponse = documentWorkerService.runSubmitWorker(batchSize);

        // then
        assertEquals(batchSize, workerResponse.getRequestedBatchSize());
        assertEquals(1, workerResponse.getSelectedCount());
        assertEquals(1, workerResponse.getSuccessCount());
        assertEquals(0, workerResponse.getFailedCount());

        verify(documentRepository).findByStatus(eq(DocumentStatus.DRAFT), any(Pageable.class));
        verify(batchDocumentService).submitBatch(eq(List.of(1L)), anyString(), anyString());
    }

    /**
     * Пакетный submit
     */
    @Test
    void runSubmitWorker_respectsBatchSize() {
        int batchSize = 3;

        // given: в БД больше документов, чем batchSize
        DocumentItem d1 = new DocumentItem(); d1.setId(1L); d1.setStatus(DocumentStatus.DRAFT);
        DocumentItem d2 = new DocumentItem(); d2.setId(2L); d2.setStatus(DocumentStatus.DRAFT);
        DocumentItem d3 = new DocumentItem(); d3.setId(3L); d3.setStatus(DocumentStatus.DRAFT);

        List<DocumentItem> pageContent = List.of(d1, d2, d3);
        long totalDraft = 10L; // имитируем, что всего таких 10 в базе

        Page<DocumentItem> page = new PageImpl<>(
                pageContent,
                PageRequest.of(0, batchSize),
                totalDraft
        );

        when(documentRepository.findByStatus(eq(DocumentStatus.DRAFT), any(Pageable.class)))
                .thenReturn(page);

        // из 3-х документов 2 успешных, 1 с ошибкой
        DocumentBatchItemResult r1 = DocumentBatchItemResult.builder()
                .documentId(1L)
                .success(true)
                .message(null)
                .build();
        DocumentBatchItemResult r2 = DocumentBatchItemResult.builder()
                .documentId(2L)
                .success(true)
                .message(null)
                .build();
        DocumentBatchItemResult r3 = DocumentBatchItemResult.builder()
                .documentId(3L)
                .success(false)
                .message("error")
                .build();
        DocumentBatchResponse response = DocumentBatchResponse.builder().results(List.of(r1, r2, r3)).build();

        when(batchDocumentService.submitBatch(
                eq(List.of(1L, 2L, 3L)),
                anyString(),
                anyString()
        )).thenReturn(response);

        // when
        WorkerRunResponse workerResponse = documentWorkerService.runSubmitWorker(batchSize);

        // then
        assertEquals(batchSize, workerResponse.getRequestedBatchSize());
        assertEquals(3, workerResponse.getSelectedCount());
        assertEquals(2, workerResponse.getSuccessCount());
        assertEquals(1, workerResponse.getFailedCount());
    }

    /**
     * Пакетный approve с частичными результатами
     */
    @Test
    void runApproveWorker_partialResults() {
        int batchSize = 4;

        // given: 4 документа в SUBMITTED
        DocumentItem d1 = new DocumentItem(); d1.setId(1L); d1.setStatus(DocumentStatus.SUBMITTED);
        DocumentItem d2 = new DocumentItem(); d2.setId(2L); d2.setStatus(DocumentStatus.SUBMITTED);
        DocumentItem d3 = new DocumentItem(); d3.setId(3L); d3.setStatus(DocumentStatus.SUBMITTED);
        DocumentItem d4 = new DocumentItem(); d4.setId(4L); d4.setStatus(DocumentStatus.SUBMITTED);

        Page<DocumentItem> page = new PageImpl<>(
                List.of(d1, d2, d3, d4),
                PageRequest.of(0, batchSize),
                4
        );

        when(documentRepository.findByStatus(eq(DocumentStatus.SUBMITTED), any(Pageable.class)))
                .thenReturn(page);

        // допустим: 1 и 3 — success, 2 и 4 — fail
        DocumentBatchItemResult r1 = DocumentBatchItemResult.builder()
                .documentId(1L)
                .success(true)
                .message(null)
                .build();
        DocumentBatchItemResult r2 = DocumentBatchItemResult.builder()
                .documentId(2L)
                .success(false)
                .message("error 2")
                .build();
        DocumentBatchItemResult r3 = DocumentBatchItemResult.builder()
                .documentId(3L)
                .success(true)
                .message(null)
                .build();
        DocumentBatchItemResult r4 = DocumentBatchItemResult.builder()
                .documentId(4L)
                .success(false)
                .message("error 4")
                .build();
        DocumentBatchResponse response = DocumentBatchResponse.builder().results(List.of(r1, r2, r3, r4)).build();

        when(batchDocumentService.approveBatch(
                eq(List.of(1L, 2L, 3L, 4L)),
                anyString(),
                anyString()
        )).thenReturn(response);

        // when
        WorkerRunResponse workerResponse = documentWorkerService.runApproveWorker(batchSize);

        // then
        assertEquals(4, workerResponse.getSelectedCount());
        assertEquals(2, workerResponse.getSuccessCount());
        assertEquals(2, workerResponse.getFailedCount());
    }

    /**
     * Ошибка approve
     */
    @Test
    void runApproveWorker_throwsIfBatchServiceThrows() {
        int batchSize = 2;

        DocumentItem d1 = new DocumentItem(); d1.setId(1L); d1.setStatus(DocumentStatus.SUBMITTED);
        DocumentItem d2 = new DocumentItem(); d2.setId(2L); d2.setStatus(DocumentStatus.SUBMITTED);

        Page<DocumentItem> page = new PageImpl<>(
                List.of(d1, d2),
                PageRequest.of(0, batchSize),
                2
        );

        when(documentRepository.findByStatus(eq(DocumentStatus.SUBMITTED), any(Pageable.class)))
                .thenReturn(page);

        when(batchDocumentService.approveBatch(anyList(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Registry failed"));

        // when / then
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> documentWorkerService.runApproveWorker(batchSize)
        );
        assertEquals("Registry failed", ex.getMessage());
    }
}
