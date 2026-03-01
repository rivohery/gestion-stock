package com.alibou.stockmanage.auths.handlers;

import com.alibou.stockmanage.auths.exceptions.ActivationTokenException;
import com.alibou.stockmanage.auths.exceptions.AuthenticationRequiredException;
import com.alibou.stockmanage.auths.exceptions.InvalidPrincipalException;
import com.alibou.stockmanage.shared.handlers.HttpErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpErrorResponse> handleException(LockedException exp) {
        return ResponseEntity
                .status(HttpStatus.LOCKED)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.LOCKED.value())
                );
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpErrorResponse> handleException(DisabledException exp) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.FORBIDDEN.value())
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpErrorResponse> handleException(AccessDeniedException exp) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        HttpErrorResponse.of(exp.getMessage() + ": some role is required to access this resource", HttpStatus.FORBIDDEN.value())
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpErrorResponse> handleException(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        HttpErrorResponse.of("Login or password Incorrect", HttpStatus.BAD_REQUEST.value())
                );
    }

    @ExceptionHandler(AuthenticationRequiredException.class)
    public ResponseEntity<HttpErrorResponse> handleException(AuthenticationRequiredException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        HttpErrorResponse.of("Full authentication is required", HttpStatus.UNAUTHORIZED.value())
                );
    }

    @ExceptionHandler(InvalidPrincipalException.class)
    public ResponseEntity<HttpErrorResponse> handleException(InvalidPrincipalException exp) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.UNAUTHORIZED.value())
                );
    }

    @ExceptionHandler(ActivationTokenException.class)
    public ResponseEntity<HttpErrorResponse> handleException(ActivationTokenException exp) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.FORBIDDEN.value())
                );
    }

}

