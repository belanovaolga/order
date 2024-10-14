package com.example.order_module.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long number;
    private LocalDateTime orderDate;
    private Long customerId;
    private Long productId;
    private String productName;
    private Double price;
    private Long count;
    private Double sum;
}
