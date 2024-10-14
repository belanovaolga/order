package com.example.order_module.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequest {
    private Long customerId;
    private Long productId;
    private Long count;
}
