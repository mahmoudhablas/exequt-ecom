package com.exequt.ecom.exception;

public class DuplicateCartItemException extends RuntimeException {
    public DuplicateCartItemException(Long productId) {
        super("Duplicate cart item for product id " + productId);
    }
}

