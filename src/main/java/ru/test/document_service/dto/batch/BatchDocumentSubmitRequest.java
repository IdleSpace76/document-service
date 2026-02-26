package ru.test.document_service.dto.batch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * DTO для отправки списка документов на субмит
 *
 * @author a.zharov
 */
@Data
public class BatchDocumentSubmitRequest {

    /**
     * Список id
     */
    @NotEmpty
    private List<Long> documentIds;

    /**
     * Автор субмита
     */
    @NotBlank
    private String performedBy;

    /**
     * Комментарий
     */
    private String comment;
}
