package com.example.order.module.controller;

import com.example.order.module.exception.MessageException;
import com.example.order.module.exception.NotEnoughGoods;
import com.example.order.module.exception.OrderNotFound;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ExceptionApiHandler {
    @ExceptionHandler(OrderNotFound.class)
    public ResponseStatusException productNotFound(OrderNotFound orderNotFound) {
        return new ResponseStatusException(HttpStatusCode.valueOf(404), "The order does not exist");
    }

    @ExceptionHandler(NotEnoughGoods.class)
    public ResponseStatusException notEnoughGoods(NotEnoughGoods notEnoughGoods) {
        return new ResponseStatusException(HttpStatusCode.valueOf(400), "There are not enough goods");
    }

    @ExceptionHandler(MessageException.class)
    public ResponseStatusException messagingException(MessageException messageException) {
        return new ResponseStatusException(HttpStatusCode.valueOf(500), "Couldn't send email");
    }
}
