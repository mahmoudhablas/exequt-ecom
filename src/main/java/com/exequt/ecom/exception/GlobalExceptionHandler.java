package com.exequt.ecom.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleCartNotFound(
            CartNotFoundException ex,
            HttpServletRequest request) {

        return buildError(
                ErrorCode.CART_NOT_FOUND,
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleProductNotFound(
            ProductNotFoundException ex,
            HttpServletRequest request) {

        return buildError(
                ErrorCode.PRODUCT_NOT_FOUND,
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request
        );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return buildError(
                ErrorCode.VALIDATION_ERROR,
                message,
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return buildError(
                ErrorCode.BAD_REQUEST,
                ex.getMessage(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }
    @ExceptionHandler(CartNotActiveException.class)
    public ResponseEntity<ErrorMessage> handleCartNotActive(
            CartNotActiveException ex,
            HttpServletRequest request) {

        return buildError(
                ErrorCode.CART_NOT_ACTIVE,
                ex.getMessage(),
                HttpStatus.UNPROCESSABLE_ENTITY,
                request
        );
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleCustomerNotFound(
            CustomerNotFoundException ex,
            HttpServletRequest request) {
        return buildError(
                ErrorCode.CUSTOMER_NOT_FOUND,
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleCartItemNotFound(
            CartItemNotFoundException ex,
            HttpServletRequest request) {
        return buildError(
                ErrorCode.CART_ITEM_NOT_FOUND,
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<ErrorMessage> handleProductOutOfStock(
            ProductOutOfStockException ex,
            HttpServletRequest request) {
        return buildError(
                ErrorCode.PRODUCT_OUT_OF_STOCK,
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request
        );
    }

    @ExceptionHandler(DuplicateCartItemException.class)
    public ResponseEntity<ErrorMessage> handleDuplicateCartItem(
            DuplicateCartItemException ex,
            HttpServletRequest request) {
        return buildError(
                ErrorCode.DUPLICATE_CART_ITEM,
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request
        );
    }

    @ExceptionHandler(CartAccessDeniedException.class)
    public ResponseEntity<ErrorMessage> handleCartAccessDenied(
            CartAccessDeniedException ex,
            HttpServletRequest request) {
        return buildError(
                ErrorCode.CART_ACCESS_DENIED,
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unhandled exception occurred", ex);

        return buildError(
                ErrorCode.INTERNAL_ERROR,
                "Unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    private ResponseEntity<ErrorMessage> buildError(
            ErrorCode code,
            String message,
            HttpStatus status,
            HttpServletRequest request) {

        // Prefer header value if present, else fallback to MDC
        String correlationId = request.getHeader("X-Correlation-Id");
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = MDC.get("correlationId");
        }

        ErrorMessage error = ErrorMessage.builder()
                .code(code.name())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .correlationId(correlationId)
                .build();

        return new ResponseEntity<>(error, status);
    }
    // add the new methods for the other exceptions here

}