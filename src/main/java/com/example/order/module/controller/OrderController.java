package com.example.order.module.controller;

import com.example.order.module.service.OrderService;
import com.example.order.module.model.OrderEntity;
import com.example.order.module.model.request.OrderCreateRequest;
import com.example.order.module.model.request.OrderUpdateRequest;
import com.example.order.module.model.response.OrderListResponse;
import com.example.order.module.model.response.PersonalOfferResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderEntity> createOrder(
            @RequestBody OrderCreateRequest orderCreateRequest
    ) {
        return ResponseEntity.ok(orderService.createOrder(orderCreateRequest));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderEntity> updateOrder(
            @PathVariable UUID orderId,
            @RequestBody OrderUpdateRequest orderUpdateRequest
    ) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderUpdateRequest));
    }

    @DeleteMapping("/{orderId}")
    public void deleteOrder(
            @PathVariable UUID orderId
    ) {
        orderService.deleteOrder(orderId);
        ResponseEntity.ok();
    }

    @GetMapping("/{customerId}/list")
    public ResponseEntity<OrderListResponse> ordersList(
            @PathVariable Long customerId
    ) {
        return ResponseEntity.ok(orderService.ordersList(customerId));
    }

    @GetMapping("/{customerId}/personal-offer")
    public ResponseEntity<PersonalOfferResponse> personalOffer(
            @PathVariable Long customerId
    ) {
        return ResponseEntity.ok(orderService.getPersonalOffer(customerId));
    }
}
