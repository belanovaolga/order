package com.example.order_module.service;

import com.example.order_module.exception.NotEnoughGoods;
import com.example.order_module.exception.OrderNotFound;
import com.example.order_module.mapper.OrderMapper;
import com.example.order_module.model.OrderEntity;
import com.example.order_module.model.request.IdRequest;
import com.example.order_module.model.request.OrderCreateRequest;
import com.example.order_module.model.request.OrderUpdateRequest;
import com.example.order_module.model.response.OrderListResponse;
import com.example.order_module.model.response.OrderResponse;
import com.example.order_module.model.response.PersonalOfferResponse;
import com.example.order_module.model.response.ProductEntityResponse;
import com.example.order_module.repository.OrderRepository;
import com.example.order_module.rest.RestConsumerImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final RestConsumerImpl restConsumerImpl;
    private final OrderMapper orderMapper;

    @Override
    public OrderEntity createOrder(OrderCreateRequest orderCreateRequest) {
        countEnough(orderCreateRequest.getProductId(), orderCreateRequest.getCount());
        ProductEntityResponse productEntityResponse = restConsumerImpl.getProduct(orderCreateRequest.getProductId());

        OrderEntity orderEntity = orderMapper.toOrderEntity(orderCreateRequest, productEntityResponse);

        orderRepository.save(orderEntity);

        return orderEntity;
    }

    @Override
    public OrderEntity updateOrder(Long orderId, OrderUpdateRequest orderUpdateRequest) {
        OrderEntity currentOrder = findById(orderId);

        Long updateCount = orderUpdateRequest.getCount();
        Long currentCount = currentOrder.getCount();

        if (!Objects.equals(currentCount, updateCount)) {
            countEnough(orderUpdateRequest.getProductId(), updateCount);
        }

        currentOrder.setProductId(orderUpdateRequest.getProductId());
        currentOrder.setCount(updateCount);

        Double price = restConsumerImpl.getProduct(orderUpdateRequest.getProductId()).getCurrentPrice();
        currentOrder.setSum(updateCount * price);

        orderRepository.save(currentOrder);

        return currentOrder;
    }

    @Override
    public OrderResponse findOrderById(Long orderId) {
        return orderMapper.toOrderResponse(findById(orderId));
    }

    @Override
    public void deleteOrder(Long orderId) {
        OrderEntity orderEntity = findById(orderId);
        orderRepository.delete(orderEntity);
    }

    @Override
    public OrderListResponse ordersList(Long customerId) {
        List<OrderEntity> orderEntities = orderRepository.findAllByCustomerId(customerId).orElseThrow(OrderNotFound::new);

        return OrderListResponse.builder()
                .orderList(orderEntities.stream().map(orderMapper::toOrderResponse).toList())
                .build();
    }

    @Override
    public PersonalOfferResponse getPersonalOffer(Long customerId) {
        List<OrderEntity> personalList = orderRepository.findAllByCustomerId(customerId).orElseThrow(OrderNotFound::new);

        if (personalList.isEmpty()) {
            return restConsumerImpl.getPOForNoOrders();
        }

        List<Long> prodId = personalList.stream()
                .filter(x -> x.getOrderDate().isAfter(LocalDateTime.now().minusMonths(1)))
                .map(OrderEntity::getProductId)
                .toList();

        return restConsumerImpl.getTwoProductsPO(IdRequest.builder().productIdList(prodId).build());
    }

    private void countEnough(
            Long productId,
            Long currentCount
    ) {
        Long productCount = restConsumerImpl.getProduct(productId).getCount();
        if (productCount < currentCount) {
            throw new NotEnoughGoods();
        }
    }

    private OrderEntity findById(Long id) {
        return orderRepository.findById(id).orElseThrow(OrderNotFound::new);
    }
}
