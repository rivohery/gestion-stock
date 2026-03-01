package com.alibou.stockmanage.sales.exceptions;

public class InsufficientQtyStockException extends RuntimeException{
    public InsufficientQtyStockException() {
    }

    public InsufficientQtyStockException(String message) {
        super(message);
    }
}
