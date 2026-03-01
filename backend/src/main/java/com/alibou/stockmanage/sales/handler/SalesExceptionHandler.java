package com.alibou.stockmanage.sales.handler;

import com.alibou.stockmanage.sales.exceptions.InsufficientQtyStockException;
import com.alibou.stockmanage.shared.handlers.HttpErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SalesExceptionHandler {

    @ExceptionHandler(InsufficientQtyStockException.class)
    public ResponseEntity<HttpErrorResponse> handleException(InsufficientQtyStockException exp) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.NOT_ACCEPTABLE.value())
                );
    }
}
