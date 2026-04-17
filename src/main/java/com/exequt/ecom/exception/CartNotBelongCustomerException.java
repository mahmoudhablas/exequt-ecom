package com.exequt.ecom.exception;

public class CartNotBelongCustomerException extends RuntimeException {
    public CartNotBelongCustomerException(Long id, Long customerId) {
         super("Cart with id " + id + " does not belong to customer with id " + customerId);
    }
}
