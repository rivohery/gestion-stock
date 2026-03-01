package com.alibou.stockmanage.email.exception;

public class SendingEmailException extends RuntimeException{
    public SendingEmailException(String message) {
        super(message);
    }
}
