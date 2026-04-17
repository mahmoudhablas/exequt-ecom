package com.exequt.ecom.exception;

public class CartAccessDeniedException extends RuntimeException {
    public CartAccessDeniedException(Long cartId) {
        super("Access denied for cart id " + cartId);
    }
}

