package ru.test.document_service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.document_service.domain.DocumentHistory;

import java.util.List;

/**
 * @author a.zharov
 */
@Repository
public interface DocumentHistoryRepository extends JpaRepository<DocumentHistory, Long> {

    List<DocumentHistory> findByDocumentIdOrderByCreatedAtDesc(Long documentId);
}
