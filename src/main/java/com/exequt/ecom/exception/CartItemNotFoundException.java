package com.exequt.ecom.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(Long cartId, Integer productId) {
        super("Cart item with product id " + productId + " and cart id " );
    }
}

