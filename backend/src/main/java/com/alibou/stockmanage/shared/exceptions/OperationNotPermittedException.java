package com.alibou.stockmanage.shared.exceptions;

public class OperationNotPermittedException extends RuntimeException{
    public OperationNotPermittedException() {
    }

    public OperationNotPermittedException(String message) {
        super(message);
    }
}
