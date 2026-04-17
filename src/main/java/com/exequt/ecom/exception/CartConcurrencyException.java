package com.exequt.ecom.exception;

public class CartConcurrencyException extends RuntimeException {
    public CartConcurrencyException(Long cartId) {
        super("Cart was updated by another session. Please reload and try again. CartId: " + cartId);
    }
}

