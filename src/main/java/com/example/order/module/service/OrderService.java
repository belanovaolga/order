package com.example.order.module.service;

import com.example.order.module.model.OrderEntity;
import com.example.order.module.model.request.OrderCreateRequest;
import com.example.order.module.model.request.OrderUpdateRequest;
import com.example.order.module.model.response.OrderListResponse;
import com.example.order.module.model.response.OrderResponse;
import com.example.order.module.model.response.PersonalOfferResponse;

public interface OrderService {
    OrderEntity createOrder(OrderCreateRequest orderCreateRequest);

    OrderEntity updateOrder(Long orderId, OrderUpdateRequest orderUpdateRequest);

    OrderResponse findOrderById(Long id);

    void deleteOrder(Long orderId);

    OrderListResponse ordersList(Long customerId);

    PersonalOfferResponse getPersonalOffer(Long customerId);
}
