package com.example.dummycrud.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@AllArgsConstructor
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
