package com.exequt.ecom.exception;

public class CartNotActiveException extends RuntimeException {
    public CartNotActiveException(Long id) {
        super("Cart not active with id " + id);
    }
}
