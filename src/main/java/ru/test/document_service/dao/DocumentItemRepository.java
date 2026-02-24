package ru.test.document_service.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.document_service.domain.DocumentItem;

import java.util.Optional;

/**
 * @author a.zharov
 */
@Repository
public interface DocumentItemRepository extends JpaRepository<DocumentItem, Long> {
    /**
     * Проверка существования УИД
     */
    boolean existsByUid(String uid);

    /**
     * Поиск по УИД
     */
    Optional<DocumentItem> findByUid(String uid);
}
