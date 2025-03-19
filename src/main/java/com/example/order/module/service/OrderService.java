package com.example.order.module.service;

import com.example.order.module.model.OrderEntity;
import com.example.order.module.model.request.OrderCreateRequest;
import com.example.order.module.model.request.OrderUpdateRequest;
import com.example.order.module.model.request.PersonalOfferListRequest;
import com.example.order.module.model.response.OrderListResponse;
import com.example.order.module.model.response.OrderResponse;
import com.example.order.module.model.response.PersonalOfferListResponse;
import com.example.order.module.model.response.PersonalOfferResponse;

import java.util.UUID;

public interface OrderService {
    OrderEntity createOrder(OrderCreateRequest orderCreateRequest);

    OrderEntity updateOrder(UUID orderId, OrderUpdateRequest orderUpdateRequest);

    OrderResponse findOrderById(UUID id);

    void deleteOrder(UUID orderId);

    OrderListResponse ordersList(Long customerId);

    PersonalOfferResponse getPersonalOffer(Long customerId);

    OrderListResponse getAllOrders();

    PersonalOfferListResponse getPersonalOfferList(PersonalOfferListRequest personalOfferListRequest);
}
