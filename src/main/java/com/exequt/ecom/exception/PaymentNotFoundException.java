package com.exequt.ecom.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String provider, String paymentId) {
        super(
                "Payment not found for provider: " + provider +
                        " and paymentId: " + paymentId);
    }
}

