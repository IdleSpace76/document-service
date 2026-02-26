package ru.test.document_service.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.test.document_service.domain.enums.DocumentStatus;

import java.time.Instant;

/**
 * @author a.zharov
 */
@Data
public class DocumentSearchFilter {

    private DocumentStatus status;
    private String author;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdTo;
}
