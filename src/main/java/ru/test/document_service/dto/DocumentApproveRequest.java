package ru.test.document_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO для утверждения документа
 *
 * @author a.zharov
 */
@Data
public class DocumentApproveRequest {

    /**
     * Автор апрува
     */
    @NotBlank
    private String approver;

    /**
     * Комментарий
     */
    private String comment;
}
