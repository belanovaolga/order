package com.example.order_module.service;

import com.example.order_module.exception.NotEnoughGoods;
import com.example.order_module.exception.OrderNotFound;
import com.example.order_module.kafka.KafkaSender;
import com.example.order_module.mapper.OrderMapper;
import com.example.order_module.model.OrderEntity;
import com.example.order_module.model.request.IdRequest;
import com.example.order_module.model.request.OrderCreateRequest;
import com.example.order_module.model.request.OrderUpdateRequest;
import com.example.order_module.model.request.ProductCountDto;
import com.example.order_module.model.response.OrderListResponse;
import com.example.order_module.model.response.OrderResponse;
import com.example.order_module.model.response.PersonalOfferResponse;
import com.example.order_module.model.response.ProductEntityResponse;
import com.example.order_module.repository.OrderRepository;
import com.example.order_module.rest.RestConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final RestConsumer restConsumer;
    private final OrderMapper orderMapper;
    private final KafkaSender kafkaSender;
    private final TransactionTemplate transactionTemplate;

    @Transactional
    @Override
    public OrderEntity createOrder(OrderCreateRequest orderCreateRequest) {
        checkProductCount(orderCreateRequest.getProductId(), orderCreateRequest.getCount());
        ProductEntityResponse productEntityResponse = restConsumer.getProduct(orderCreateRequest.getProductId());

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

//        Можно сначала сходить в рест например, а потом уже открыть транзакцию поработать с БД и закрыть

        Long updateCount = orderUpdateRequest.getCount();
        Long currentCount = currentOrder.getCount();
        Long currentProductId = currentOrder.getProductId();
        long difference = updateCount - currentCount;

        if (orderUpdateRequest.getProductId().equals(currentOrder.getProductId())) {
            if (!Objects.equals(currentCount, updateCount)) {
                boolean deleteProduct = currentCount < updateCount;
                if (deleteProduct) {
                    checkProductCount(orderUpdateRequest.getProductId(), difference);
                }

                Double price = restConsumer.getProduct(orderUpdateRequest.getProductId()).getCurrentPrice();

                currentOrder.setCount(updateCount);
                currentOrder.setSum(updateCount * price);
                transactionTemplate.execute(status -> {
                    saveOrder(currentOrder);
                    return null;
                });
//                orderRepository.save(currentOrder);

                Long productCount = difference > 0 ? difference : -difference;
                kafkaSender.sendProductCount(ProductCountDto.builder()
                        .productId(orderUpdateRequest.getProductId())
                        .count(productCount)
                        .deleteProduct(deleteProduct)
                        .build());
            }

        } else {
            checkProductCount(orderUpdateRequest.getProductId(), updateCount);

            ProductEntityResponse productEntityResponse = restConsumer.getProduct(orderUpdateRequest.getProductId());
            Double price = productEntityResponse.getCurrentPrice();

            currentOrder.setProductId(orderUpdateRequest.getProductId());
            currentOrder.setProductName(productEntityResponse.getName());
            currentOrder.setPrice(price);
            currentOrder.setCount(updateCount);
            currentOrder.setSum(updateCount * price);
            transactionTemplate.execute(status -> {
                saveOrder(currentOrder);
                return null;
            });
//            orderRepository.save(currentOrder);

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

    private void saveOrder(OrderEntity orderEntity) {
        orderRepository.save(orderEntity);
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
        List<OrderEntity> orderEntities = orderRepository.findAllByCustomerId(customerId).orElseThrow(OrderNotFound::new);

        return OrderListResponse.builder()
                .orderList(orderEntities.stream().map(orderMapper::toOrderResponse).toList())
                .build();
    }

    @Override
    public PersonalOfferResponse getPersonalOffer(Long customerId) {
        List<OrderEntity> personalList = orderRepository.findAllByCustomerId(customerId).orElseThrow(OrderNotFound::new);

        if (personalList.isEmpty()) {
            return restConsumer.getPOForNoOrders();
        }

        List<Long> prodId = personalList.stream()
                .filter(x -> x.getOrderDate().isAfter(LocalDateTime.now().minusMonths(1)))
                .map(OrderEntity::getProductId)
                .toList();

        return restConsumer.getTwoProductsPO(IdRequest.builder().productIdList(prodId).build());
    }

    private void checkProductCount(
            Long productId,
            Long currentCount
    ) {
        Long productCount = restConsumer.getProduct(productId).getCount();
        if (productCount < currentCount) {
            throw new NotEnoughGoods();
        }
    }

    private OrderEntity findById(Long id) {
        return orderRepository.findById(id).orElseThrow(OrderNotFound::new);
    }
}
