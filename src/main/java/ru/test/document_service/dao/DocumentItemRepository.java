package ru.test.document_service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.test.document_service.domain.DocumentItem;

/**
 * @author a.zharov
 */
@Repository
public interface DocumentItemRepository extends
        JpaRepository<DocumentItem, Long>,
        JpaSpecificationExecutor<DocumentItem> {
}
