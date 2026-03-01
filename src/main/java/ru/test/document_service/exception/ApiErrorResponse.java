package ru.test.document_service.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * @author a.zharov
 */
@Getter
@Builder
public class ApiErrorResponse {

    /**
     * Код ошибки
     */
    String code;

    /**
     * Сообщение
     */
    String message;

    /**
     * Время
     */
    Instant timestamp;
}
