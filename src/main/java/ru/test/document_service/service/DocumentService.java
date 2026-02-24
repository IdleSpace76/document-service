package ru.test.document_service.service;

import ru.test.document_service.domain.DocumentItem;

import java.util.List;

/**
 * Работа с сущностью документа
 *
 * @author a.zharov
 */
public interface DocumentService {

    /**
     * Создать документ
     */
    DocumentItem createDocument(String author, String title);

    /**
     * Найти документ по id
     */
    DocumentItem getById(Long id);

    /**
     * Найти документ по uid
     */
    DocumentItem getByUid(String uid);

    /**
     * Получить все документы
     */
    List<DocumentItem> getAll();
}
