package com.alibou.stockmanage.auths.exceptions;

public class ActivationTokenException extends RuntimeException{
    public ActivationTokenException() {
    }

    public ActivationTokenException(String message) {
        super(message);
    }
}
