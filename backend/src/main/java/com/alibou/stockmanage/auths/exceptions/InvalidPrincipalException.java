package com.alibou.stockmanage.auths.exceptions;

public class InvalidPrincipalException extends RuntimeException{
    public InvalidPrincipalException() {
    }

    public InvalidPrincipalException(String message) {
        super(message);
    }
}
