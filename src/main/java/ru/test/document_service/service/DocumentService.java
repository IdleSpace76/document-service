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
     * Получить все документы
     */
    List<DocumentItem> getAll();

    /**
     * Отправить документ на утверждение (SUBMIT)
     */
    DocumentItem submit(Long id, String performedBy, String comment);

    /**
     * Утвердить документ (APPROVE)
     */
    DocumentItem approve(Long id, String approver, String comment);
}
