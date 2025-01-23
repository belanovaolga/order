package com.example.order.module.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntityResponse {
    private Long id;
    private String name;
    private String description;
    private Long count;
    private Double currentPrice;
}
