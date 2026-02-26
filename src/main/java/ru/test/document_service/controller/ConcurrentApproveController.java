package ru.test.document_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.test.document_service.dto.concurrent.ConcurrentApproveRequest;
import ru.test.document_service.dto.concurrent.ConcurrentApproveResponse;
import ru.test.document_service.service.ConcurrentApproveService;

/**
 * @author a.zharov
 */
@RestController
@RequestMapping("/api/documents/concurrent-approve")
@RequiredArgsConstructor
public class ConcurrentApproveController {

    private final ConcurrentApproveService concurrentApproveService;

    /**
     * Запуск конкурентного approve для одного документа
     */
    @PostMapping
    public ConcurrentApproveResponse runTest(@Valid @RequestBody ConcurrentApproveRequest request)
            throws InterruptedException {
        return concurrentApproveService.concurrentApprove(request);
    }
}
