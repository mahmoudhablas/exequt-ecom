package com.exequt.ecom.exception;

public class NoActiveCartForCustomer extends RuntimeException {
    public NoActiveCartForCustomer(Long customerId) {
        super("There is no active cart and belongs to the customer with id " + customerId);
    }
}
