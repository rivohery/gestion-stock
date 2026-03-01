package com.alibou.stockmanage.auths.exceptions;

public class AuthenticationRequiredException extends RuntimeException{
    public AuthenticationRequiredException() {
    }

    public AuthenticationRequiredException(String message) {
        super(message);
    }
}
