package com.exequt.ecom.exception;

public class CartEmptyException extends RuntimeException {
    public CartEmptyException(Long cartId) {
        super("Cart is empty. Cannot proceed with operation. CartId: " + cartId);
    }
}

