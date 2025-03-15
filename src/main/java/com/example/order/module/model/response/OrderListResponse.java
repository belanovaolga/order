package com.example.order.module.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderListResponse {
    List<OrderResponse> orderList;
}
