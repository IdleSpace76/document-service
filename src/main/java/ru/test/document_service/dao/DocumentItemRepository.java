package ru.test.document_service.dao;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.test.document_service.domain.DocumentItem;
import ru.test.document_service.domain.enums.DocumentStatus;

import java.util.List;
import java.util.Optional;

/**
 * @author a.zharov
 */
@Repository
public interface DocumentItemRepository extends
        JpaRepository<DocumentItem, Long>,
        JpaSpecificationExecutor<DocumentItem> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from DocumentItem d where d.id = :id")
    Optional<DocumentItem> findByIdForUpdate(Long id);

    Page<DocumentItem> findByStatus(DocumentStatus status, Pageable pageable);

    Page<DocumentItem> findByIdIn(List<Long> ids, Pageable pageable);

    long countByStatus(DocumentStatus status);
}
