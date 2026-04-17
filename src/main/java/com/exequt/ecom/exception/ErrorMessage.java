package com.exequt.ecom.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorMessage {
    private String code;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private String correlationId;
}