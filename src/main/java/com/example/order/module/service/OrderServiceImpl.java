package com.example.order.module.service;

import com.example.order.module.exception.DatabaseException;
import com.example.order.module.kafka.KafkaSender;
import com.example.order.module.mapper.OrderMapper;
import com.example.order.module.model.OrderEntity;
import com.example.order.module.model.request.*;
import com.example.order.module.model.response.*;
import com.example.order.module.repository.OrderRepository;
import com.example.order.module.rest.ClientProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ClientProduct restConsumerProduct;
    private final OrderMapper orderMapper;
    private final KafkaSender kafkaSender;


    @Transactional
    @Override
    public OrderEntity createOrder(OrderCreateRequest orderCreateRequest) {
        checkProductCount(orderCreateRequest.getProductId(), orderCreateRequest.getCount());
        ProductEntityResponse productEntityResponse = restConsumerProduct.getProduct(orderCreateRequest.getProductId());

        OrderEntity orderEntity = orderMapper.toOrderEntity(orderCreateRequest, productEntityResponse);

        orderRepository.save(orderEntity);
        kafkaSender.sendProductCount(ProductCountDto.builder()
                .productId(orderCreateRequest.getProductId())
                .count(orderCreateRequest.getCount())
                .deleteProduct(true)
                .build());

        return orderEntity;
    }

    @Transactional
    @Override
    public OrderEntity updateOrder(Long orderId, OrderUpdateRequest orderUpdateRequest) {
        OrderEntity currentOrder = findById(orderId);

        Long updateCount = orderUpdateRequest.getCount();
        Long currentCount = currentOrder.getCount();
        Long currentProductId = currentOrder.getProductId();
        long difference = updateCount - currentCount;

        if (orderUpdateRequest.getProductId().equals(currentOrder.getProductId())) {
            if (!Objects.equals(currentCount, updateCount)) {
                boolean deleteProduct = currentCount < updateCount;
                if (deleteProduct) {
                    countEnough(orderUpdateRequest.getProductId(), difference);
                }

                Double price = restConsumerProduct.getProduct(orderUpdateRequest.getProductId()).getCurrentPrice();

                currentOrder.setCount(updateCount);
                currentOrder.setSum(updateCount * price);
                orderRepository.save(currentOrder);

                Long productCount = difference > 0 ? difference : -difference;
                kafkaSender.sendProductCount(ProductCountDto.builder()
                        .productId(orderUpdateRequest.getProductId())
                        .count(productCount)
                        .deleteProduct(deleteProduct)
                        .build());
            }

        } else {
            checkProductCount(orderUpdateRequest.getProductId(), updateCount);

            ProductEntityResponse productEntityResponse = restConsumerProduct.getProduct(orderUpdateRequest.getProductId());
            Double price = productEntityResponse.getCurrentPrice();

            currentOrder.setProductId(orderUpdateRequest.getProductId());
            currentOrder.setProductName(productEntityResponse.getName());
            currentOrder.setPrice(price);
            currentOrder.setCount(updateCount);
            currentOrder.setSum(updateCount * price);
            orderRepository.save(currentOrder);

            kafkaSender.sendProductCount(ProductCountDto.builder()
                    .productId(currentProductId)
                    .count(currentCount)
                    .deleteProduct(false)
                    .build());

            kafkaSender.sendProductCount(ProductCountDto.builder()
                    .productId(orderUpdateRequest.getProductId())
                    .count(updateCount)
                    .deleteProduct(true)
                    .build());
        }

        return currentOrder;
    }

    @Override
    public OrderResponse findOrderById(Long orderId) {
        return orderMapper.toOrderResponse(findById(orderId));
    }

    @Transactional
    @Override
    public void deleteOrder(Long orderId) {
        OrderEntity orderEntity = findById(orderId);
        orderRepository.delete(orderEntity);
        kafkaSender.sendProductCount(ProductCountDto.builder()
                .productId(orderEntity.getProductId())
                .count(orderEntity.getCount())
                .deleteProduct(false)
                .build());
    }

    @Override
    public OrderListResponse ordersList(Long customerId) {
        List<OrderEntity> orderEntities = orderRepository.findAllByCustomerId(customerId).orElseThrow(() -> new DatabaseException("The order does not exist", 404));

        return OrderListResponse.builder()
                .orderList(orderEntities.stream().map(orderMapper::toOrderResponse).toList())
                .build();
    }

    @Override
    public PersonalOfferResponse getPersonalOffer(Long customerId) {
        List<OrderEntity> personalList = orderRepository.findAllByCustomerId(customerId).orElseThrow(() -> new DatabaseException("The order does not exist", 404));

        if (personalList.isEmpty()) {
            return restConsumerProduct.getPersonalOffer(IdRequest.builder().productIdList(new ArrayList<>()).build());
        }

        List<Long> prodId = personalList.stream()
                .filter(x -> x.getOrderDate().isAfter(LocalDateTime.now().minusMonths(1)))
                .map(OrderEntity::getProductId)
                .toList();

        return restConsumerProduct.getPersonalOffer(IdRequest.builder().productIdList(prodId).build());
    }

    @Override
    public OrderListResponse getAllOrders() {
        List<OrderEntity> orderEntityList = findAll();
        return OrderListResponse.builder().orderList(orderMapper.toOrderResponseList(orderEntityList)).build();
    }

    @Override
    public PersonalOfferListResponse getPersonalOfferList(PersonalOfferListRequest personalOfferListRequest) {
        return restConsumerProduct.getPersonalOfferList(personalOfferListRequest);
    }

    private void countEnough(
            Long productId,
            Long currentCount
    ) {
        Long productCount = restConsumerProduct.getProduct(productId).getCount();
        if (productCount < currentCount) {
            throw new DatabaseException("There are not enough goods", 400);
        }
    }

    private OrderEntity findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new DatabaseException("The order does not exist", 404));
    }

    private List<OrderEntity> findAll() {
        return orderRepository.findAll();
    }
}
