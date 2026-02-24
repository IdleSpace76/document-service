package ru.test.document_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.test.document_service.dao.DocumentItemRepository;
import ru.test.document_service.domain.DocumentItem;
import ru.test.document_service.service.DocumentService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * @author a.zharov
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentItemRepository documentRepository;

    @Override
    @Transactional
    public DocumentItem createDocument(String author, String title) {
        String uid = generateUniqueUid();

        DocumentItem document = new DocumentItem();
        // Если есть вероятность коллизии УИДа, можно сделать повторную генерацию со счетчиком
        document.setUid(uid);
        document.setAuthor(author);
        document.setTitle(title);
        return documentRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentItem getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Документ по id не найден, id = " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentItem getByUid(String uid) {
        return documentRepository.findByUid(uid)
                .orElseThrow(() -> new NoSuchElementException("Документ по uid не найден, uid = " + uid));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentItem> getAll() {
        return documentRepository.findAll();
    }

    /**
     * Генерация УИД
     */
    private String generateUniqueUid() {
        return UUID.randomUUID().toString();
    }
}
