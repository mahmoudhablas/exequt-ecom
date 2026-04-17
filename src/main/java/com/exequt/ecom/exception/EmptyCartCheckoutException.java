package com.exequt.ecom.exception;

public class EmptyCartCheckoutException extends RuntimeException {
    public EmptyCartCheckoutException(Long cartId) {
        super("Cannot checkout an empty cart. Cart id: " + cartId);
    }
}

