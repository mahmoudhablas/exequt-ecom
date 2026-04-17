package com.exequt.ecom.exception;

public class ProductOutOfStockException extends RuntimeException {
    public ProductOutOfStockException(Integer id) {
        super("Product with id " + id + " is out of stock.");
    }
}
