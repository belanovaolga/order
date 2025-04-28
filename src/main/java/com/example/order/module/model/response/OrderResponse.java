package com.example.order.module.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private Long number;
    private Instant orderDate;
    private Long customerId;
    private Long productId;
    private String productName;
    private Double price;
    private Long count;
    private Double sum;
}
