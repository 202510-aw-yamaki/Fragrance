package com.fregrance.app.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    String code,
    String message,
    LocalDateTime timestamp,
    Map<String, String> fieldErrors
) {
}
