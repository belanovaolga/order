package com.example.order.module.controller;

import com.example.order.module.exception.EmailException;
import com.example.order.module.exception.DatabaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ExceptionApiHandler {
    @ExceptionHandler(DatabaseException.class)
    public ResponseStatusException databaseException(DatabaseException databaseException) {
        log.debug(Arrays.toString(databaseException.getStackTrace()));
        return new ResponseStatusException(HttpStatusCode.valueOf(databaseException.getCode()), databaseException.getMessage());
    }

    @ExceptionHandler(EmailException.class)
    public ResponseStatusException emailException(EmailException emailException) {
        log.debug(Arrays.toString(emailException.getStackTrace()));
        return new ResponseStatusException(HttpStatusCode.valueOf(emailException.getCode()), emailException.getMessage());
    }
}
