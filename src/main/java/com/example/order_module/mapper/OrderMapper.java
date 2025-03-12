package com.example.order_module.mapper;

import com.example.order_module.model.OrderEntity;
import com.example.order_module.model.request.OrderCreateRequest;
import com.example.order_module.model.response.OrderResponse;
import com.example.order_module.model.response.ProductEntityResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class OrderMapper {
    public OrderEntity toOrderEntity(OrderCreateRequest orderCreateRequest, ProductEntityResponse productEntityResponse) {
        return OrderEntity.builder()
                .customerId(orderCreateRequest.getCustomerId())
                .productId(orderCreateRequest.getProductId())
                .productName(productEntityResponse.getName())
                .price(productEntityResponse.getCurrentPrice())
                .sum(orderCreateRequest.getCount() * productEntityResponse.getCurrentPrice())
                .count(orderCreateRequest.getCount())
                .orderDate(Instant.now())
                .build();
    }

    public OrderResponse toOrderResponse(OrderEntity orderEntity) {
        return OrderResponse.builder()
                .id(orderEntity.getId())
                .number(orderEntity.getNumber())
                .orderDate(orderEntity.getOrderDate())
                .customerId(orderEntity.getCustomerId())
                .productId(orderEntity.getProductId())
                .productName(orderEntity.getProductName())
                .price(orderEntity.getPrice())
                .count(orderEntity.getCount())
                .sum(orderEntity.getSum())
                .build();
    }
}
