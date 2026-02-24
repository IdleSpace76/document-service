package ru.test.document_service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.document_service.domain.ApprovalRegistryRecord;

import java.util.Optional;

/**
 * @author a.zharov
 */
@Repository
public interface ApprovalRegistryRecordRepository extends JpaRepository<ApprovalRegistryRecord, Long> {

    Optional<ApprovalRegistryRecord> findByDocumentId(Long documentId);
}
