package ru.test.document_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO для отправки документа на субмит
 *
 * @author a.zharov
 */
@Data
public class DocumentSubmitRequest {

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
