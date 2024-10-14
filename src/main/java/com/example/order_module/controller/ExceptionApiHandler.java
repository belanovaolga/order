package com.example.order_module.controller;

import com.example.order_module.exception.NotEnoughGoods;
import com.example.order_module.exception.OrderNotFound;
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
}
