package com.alibou.stockmanage.shared.handlers;

import com.alibou.stockmanage.shared.exceptions.OperationNotPermittedException;
import com.alibou.stockmanage.email.exception.SendingEmailException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SendingEmailException.class)
    public ResponseEntity<HttpErrorResponse> handleException(SendingEmailException exp) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())
                );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> handleException(EntityNotFoundException exp) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.NOT_FOUND.value())
                );
    }

    @ExceptionHandler(OperationNotPermittedException.class)
    public ResponseEntity<HttpErrorResponse> handleException(OperationNotPermittedException exp) {
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.NOT_ACCEPTABLE.value())
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {
        List<String> errors = new ArrayList<>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    //var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });

        String errorMsg = "Data Submitted Invalid";
        if(!errors.isEmpty()){
            errorMsg = String.join("-", errors);
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        HttpErrorResponse.of(errorMsg, HttpStatus.BAD_REQUEST.value())
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorResponse> handleException(Exception exp) {
        exp.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        HttpErrorResponse.of(exp.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())
                );
    }
}

