package com.exequt.ecom.exception;

public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(String message) {
        super("Order is not in a valid state for payment. Current status: " + message);
    }
}

