package ru.test.document_service.dao;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.test.document_service.domain.DocumentItem;

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
}
