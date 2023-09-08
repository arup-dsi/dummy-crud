package com.example.dummycrud.exceptionHandler;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.dummycrud.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException ex) {
        log.info("CustomExceptionHandler :: handleCustomException");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

//    @ExceptionHandler(CustomException.class)
//    public ResponseEntity<Void> handleCustomExceptionVoid(CustomException ex) {
//        log.info("CustomExceptionHandler :: handleCustomExceptionVoid");
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//    }

//    @ExceptionHandler(NotFoundException.class)
//    public ResponseEntity<Void> handleNotFoundException(NotFoundException ex) {
//        log.info("CustomExceptionHandler :: handleNotFoundException");
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//    }
}



