package ru.test.document_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO для создания документа
 *
 * @author a.zharov
 */
@Data
public class DocumentCreateRequest {

    @NotBlank
    private String author;

    @NotBlank
    private String title;
}
