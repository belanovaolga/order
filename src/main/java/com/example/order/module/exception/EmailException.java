package com.example.order.module.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EmailException extends RuntimeException {
    private final String message;
    private final Integer code;
}