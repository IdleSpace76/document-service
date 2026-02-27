package ru.test.document_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.test.document_service.dao.ApprovalRegistryRecordRepository;
import ru.test.document_service.domain.DocumentItem;
import ru.test.document_service.domain.enums.DocumentStatus;
import ru.test.document_service.dto.concurrent.ConcurrentApproveRequest;
import ru.test.document_service.dto.concurrent.ConcurrentApproveResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
public class ConcurrentApproveService {

    private final DocumentService documentService;
    private final ApprovalRegistryRecordRepository approvalRegistryRepository;

    @Transactional(readOnly = true)
    public ConcurrentApproveResponse concurrentApprove(ConcurrentApproveRequest request) {
        int threads = request.getThreads();
        int attemptsPerThread = request.getAttemptsPerThread();
        int totalAttempts = threads * attemptsPerThread;

        // счетчик успехов
        AtomicInteger success = new AtomicInteger();
        // счетчик конфликтов
        AtomicInteger conflict = new AtomicInteger();
        // ошибки
        AtomicInteger error = new AtomicInteger();

        try (ExecutorService executor = Executors.newFixedThreadPool(threads)) {
            CountDownLatch latch = new CountDownLatch(threads);

            for (int t = 0; t < threads; t++) {
                executor.submit(() -> {
                    try {
                        for (int i = 0; i < attemptsPerThread; i++) {
                            try {
                                documentService.approve(
                                        request.getDocumentId(),
                                        request.getApprover(),
                                        request.getComment()
                                );
                                success.incrementAndGet();
                            }
                            catch (IllegalStateException ex) {
                                // неправильный статус
                                conflict.incrementAndGet();
                            }
                            catch (Exception ex) {
                                // любые ошибки
                                error.incrementAndGet();
                            }
                        }
                    }
                    finally {
                        latch.countDown();
                    }
                });
            }

            // ждём завершения всех потоков
            boolean completed;
            try {
                completed = latch.await(1, TimeUnit.MINUTES);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Метод был прерван", e);
            }
            if (!completed) {
                throw new IllegalStateException("Таймаут в работе метода");
            }
        }

        // читаем финальное состояние документа и количество записей в реестре
        DocumentItem finalDoc = documentService.getById(request.getDocumentId());
        DocumentStatus finalStatus = finalDoc.getStatus();
        long registryCount = approvalRegistryRepository.countByDocumentId(request.getDocumentId());

        return ConcurrentApproveResponse.builder()
                .threads(threads)
                .attemptsPerThread(attemptsPerThread)
                .totalAttempts(totalAttempts)
                .successCount(success.get())
                .conflictCount(conflict.get())
                .errorCount(error.get())
                .finalStatus(finalStatus)
                .registryRecordsCount(registryCount)
                .build();
    }
}
