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
     * Машинно-читаемый код ошибки (для клиента).
     */
    String code;

    /**
     * Человеко-читаемое сообщение.
     */
    String message;

    /**
     * Время возникновения ошибки.
     */
    Instant timestamp;
}
