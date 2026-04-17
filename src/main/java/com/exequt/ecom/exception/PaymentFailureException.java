package com.exequt.ecom.exception;

public class PaymentFailureException extends RuntimeException {
    public PaymentFailureException(String message) {
        super(message);
    }
    public PaymentFailureException(String message, Throwable cause) {
        super("Payment processing failed: " + message, cause);
    }
}

