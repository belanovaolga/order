package com.example.order_module.model.response;

import com.example.order_module.model.OrderEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderListResponse {
    List<OrderResponse> orderList;
}
