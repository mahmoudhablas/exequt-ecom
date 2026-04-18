package com.exequt.ecom.exception;

public class UnknowPaymentFailureException extends RuntimeException {
    public UnknowPaymentFailureException() {
        super("Unknown payment failure occurred (possibly a timeout or network issue). Please contact support.");
    }
}

