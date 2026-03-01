package ru.test.document_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import ru.test.document_service.dao.ApprovalRegistryRecordRepository;
import ru.test.document_service.dao.DocumentHistoryRepository;
import ru.test.document_service.dao.DocumentItemRepository;
import ru.test.document_service.domain.ApprovalRegistryRecord;
import ru.test.document_service.domain.DocumentHistory;
import ru.test.document_service.domain.DocumentItem;
import ru.test.document_service.domain.enums.DocumentStatus;
import ru.test.document_service.service.DocumentService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author a.zharov
 */
@SpringBootTest
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles("test")
public class ApproveRollbackOnRegistryErrorTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentItemRepository documentItemRepository;

    @Autowired
    private DocumentHistoryRepository historyRepository;

    // Подменяем реальный бин репозитория реестра мок-бином
    @MockitoBean
    private ApprovalRegistryRecordRepository approvalRegistryRepository;

    @Test
    void approve_shouldRollbackWhenRegistrySaveFails() {
        // given: документ в статусе SUBMITTED в реальной БД
        DocumentItem doc = new DocumentItem();
        doc.setTitle("Test doc");
        doc.setStatus(DocumentStatus.SUBMITTED);
        doc.setUid(UUID.randomUUID().toString());
        doc.setAuthor("test author");
        doc = documentItemRepository.save(doc);

        Long docId = doc.getId();

        // и имитация падения записи в реестр
        when(approvalRegistryRepository.save(any(ApprovalRegistryRecord.class)))
                .thenThrow(new RuntimeException("Simulated registry failure"));

        // when / then: approve бросает исключение
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                documentService.approve(docId, "approver", "comment")
        );
        // при желании можешь проверить текст
        assertTrue(ex.getMessage().contains("Simulated registry failure"));

        // reload: читаем состояние документа из БД после отката транзакции
        DocumentItem reloaded = documentItemRepository.findById(doc.getId())
                .orElseThrow(() -> new AssertionError("Документ должен существовать"));

        // статус НЕ сменился на APPROVED — транзакция откатилась
        assertEquals(DocumentStatus.SUBMITTED, reloaded.getStatus());

        // история не должна содержать записей по этому документу
        List<DocumentHistory> history = historyRepository.findAll();
        assertTrue(history.isEmpty(),
                "История должна быть пустой, т.к. approve откатился целиком");
    }
}
