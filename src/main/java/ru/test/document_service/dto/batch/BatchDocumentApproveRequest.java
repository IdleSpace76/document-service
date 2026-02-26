package ru.test.document_service.dto.batch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * DTO для апрува списка документов
 *
 * @author a.zharov
 */
@Data
public class BatchDocumentApproveRequest {

    /**
     * Список id документов
     */
    @NotEmpty
    private List<Long> documentIds;

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
