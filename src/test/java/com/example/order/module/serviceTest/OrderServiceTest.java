package com.example.order.module.serviceTest;

import com.example.order.module.exception.DatabaseException;
import com.example.order.module.kafka.KafkaSender;
import com.example.order.module.mapper.OrderMapper;
import com.example.order.module.mapper.OrderMapperImpl;
import com.example.order.module.model.OrderEntity;
import com.example.order.module.model.request.IdRequest;
import com.example.order.module.model.request.OrderCreateRequest;
import com.example.order.module.model.request.OrderUpdateRequest;
import com.example.order.module.model.request.ProductCountDto;
import com.example.order.module.model.response.OrderListResponse;
import com.example.order.module.model.response.OrderResponse;
import com.example.order.module.model.response.PersonalOfferResponse;
import com.example.order.module.model.response.ProductEntityResponse;
import com.example.order.module.repository.OrderRepository;
import com.example.order.module.rest.ClientProductImpl;
import com.example.order.module.service.OrderService;
import com.example.order.module.service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderServiceTest {
    private final OrderService orderService;
    private final OrderRepository mockOrderRepository;
    private final ClientProductImpl mockRestConsumerImpl;
    private final KafkaSender mockKafkaSender;
    private final OrderMapper orderMapper;
    private final LocalDateTime dataTime;
    private final OrderEntity order1;
    private final OrderEntity order2;
    private final OrderEntity orderNew;
    private final ProductEntityResponse product1;
    private final ProductEntityResponse product2;

    public OrderServiceTest() {
        mockOrderRepository = Mockito.mock(OrderRepository.class);
        orderMapper = new OrderMapperImpl();
        mockRestConsumerImpl = Mockito.mock(ClientProductImpl.class);
        mockKafkaSender = Mockito.mock(KafkaSender.class);
        orderService = new OrderServiceImpl(mockOrderRepository, mockRestConsumerImpl, orderMapper, mockKafkaSender);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        dataTime = LocalDateTime.parse(LocalDateTime.now().format(formatter), formatter);
        order1 = OrderEntity.builder().orderDate(dataTime).customerId(1L).productId(1L).productName("apple").price(89.99).count(1L).sum(89.99).build();
        order2 = OrderEntity.builder().orderDate(dataTime).customerId(1L).productId(2L).productName("lemon").price(119.99).count(1L).sum(119.99).build();
        orderNew = OrderEntity.builder().id(1L).orderDate(dataTime).customerId(1L).productId(1L).productName("apple").price(89.99).count(2L).sum(179.98).build();
        product1 = ProductEntityResponse.builder().id(1L).name("apple").description("gold apple").count(45L).currentPrice(89.99).build();
        product2 = ProductEntityResponse.builder().id(2L).name("lemon").description("soul lemon").count(76L).currentPrice(119.99).build();
    }

    @Test
    void shouldCreateOrder() {
        OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder().customerId(1L).productId(1L).count(2L).build();
        Mockito.when(mockRestConsumerImpl.getProduct(product1.getId())).thenReturn(product1);
        OrderEntity orderEntity = orderMapper.toOrderEntity(orderCreateRequest, product1);
        Mockito.when(mockOrderRepository.save(orderEntity)).thenReturn(orderNew);
        Mockito.doNothing().when(mockKafkaSender).sendProductCount(ProductCountDto.builder().productId(product1.getId()).count(product1.getCount()).deleteProduct(true).build());

        OrderEntity actualOrder = orderService.createOrder(orderCreateRequest);
        actualOrder.setId(1L);
        actualOrder.setOrderDate(dataTime);

        assertEquals(orderNew, actualOrder);
    }

    @Test
    void shouldUpdateOrder() {
        OrderUpdateRequest orderUpdateRequest = OrderUpdateRequest.builder().productId(1L).count(2L).build();
        Mockito.when(mockOrderRepository.findById(order2.getId())).thenReturn(Optional.of(order2));
        Mockito.when(mockRestConsumerImpl.getProduct(product1.getId())).thenReturn(product1);
        Mockito.when(mockOrderRepository.save(order2)).thenReturn(order2);
        Mockito.doNothing().when(mockKafkaSender).sendProductCount(ProductCountDto.builder().productId(product1.getId()).count(product1.getCount()).deleteProduct(false).build());
        Mockito.doNothing().when(mockKafkaSender).sendProductCount(ProductCountDto.builder().productId(product1.getId()).count(product1.getCount()).deleteProduct(true).build());

        OrderEntity actualOrder = orderService.updateOrder(order2.getId(), orderUpdateRequest);

        assertEquals(order2, actualOrder);
    }

    @Test
    void shouldFindOrderById() {
        OrderResponse orderResponse = orderMapper.toOrderResponse(order1);
        Mockito.when(mockOrderRepository.findById(order1.getId())).thenReturn(Optional.of(order1));

        OrderResponse actualOrderResponse = orderService.findOrderById(order1.getId());

        assertEquals(orderResponse, actualOrderResponse);
    }

    @Test
    void shouldFindOrderById_whenOrderNotFound() {
        Mockito.when(mockOrderRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(DatabaseException.class, () -> {
            orderService.findOrderById(20L);
        });
    }

    @Test
    void shouldDeleteOrder() {
        Mockito.when(mockOrderRepository.findById(order1.getId())).thenReturn(Optional.of(order1));
        Mockito.doNothing().when(mockOrderRepository).delete(order1);
        orderService.deleteOrder(order1.getId());

        Mockito.when(mockOrderRepository.findById(order1.getId())).thenReturn(Optional.empty());
        Mockito.doNothing().when(mockKafkaSender).sendProductCount(ProductCountDto.builder().productId(product1.getId()).count(product1.getCount()).deleteProduct(false).build());

        assertThrows(DatabaseException.class, () -> {
            orderService.findOrderById(product1.getId());
        });
    }

    @Test
    void shouldOrdersList() {
        List<OrderEntity> productList = new ArrayList<>();
        productList.add(order1);
        productList.add(order2);
        List<OrderResponse> expectedOrderResponseList = productList.stream().map(orderMapper::toOrderResponse).toList();

        Mockito.when(mockOrderRepository.findAllByCustomerId(1L)).thenReturn(Optional.of(productList));

        OrderListResponse actualOrderListResponse = orderService.ordersList(1L);

        assertEquals(expectedOrderResponseList, actualOrderListResponse.getOrderList());
    }

    @Test
    void shouldGetPersonalOffer() {
        List<ProductEntityResponse> personalOfferList = new ArrayList<>();
        personalOfferList.add(ProductEntityResponse.builder().id(product1.getId()).name(product1.getName()).description(product1.getDescription()).count(product1.getCount()).currentPrice(product1.getCurrentPrice()).build());
        personalOfferList.add(ProductEntityResponse.builder().id(product2.getId()).name(product2.getName()).description(product2.getDescription()).count(product2.getCount()).currentPrice(product2.getCurrentPrice()).build());
        PersonalOfferResponse expectedPersonalOfferResponse = PersonalOfferResponse.builder().personalOfferList(personalOfferList).build();

        List<OrderEntity> orderEntityList = new ArrayList<>();
        orderEntityList.add(order1);
        orderEntityList.add(order2);
        Mockito.when(mockOrderRepository.findAllByCustomerId(order1.getCustomerId())).thenReturn(Optional.of(orderEntityList));
        List<Long> productIdList = new ArrayList<>();
        productIdList.add(product1.getId());
        productIdList.add(product2.getId());
        Mockito.when(mockRestConsumerImpl.getPersonalOffer(IdRequest.builder().productIdList(productIdList).build())).thenReturn(expectedPersonalOfferResponse);

        PersonalOfferResponse actualPersonalOfferResponse = orderService.getPersonalOffer(order1.getCustomerId());

        assertEquals(expectedPersonalOfferResponse, actualPersonalOfferResponse);
    }

    @Test
    void shouldGetPersonalOffer_whenOrderNotFound() {
        Mockito.when(mockOrderRepository.findAllByCustomerId(order1.getCustomerId())).thenReturn(Optional.empty());

        assertThrows(DatabaseException.class, () -> {
            orderService.getPersonalOffer(order1.getCustomerId());
        });
    }

    @Test
    void shouldGetPersonalOffer_whenNoOrders() {
        List<ProductEntityResponse> personalOfferList = new ArrayList<>();
        PersonalOfferResponse expectedPersonalOfferResponse = PersonalOfferResponse.builder().personalOfferList(personalOfferList).build();

        Mockito.when(mockOrderRepository.findAllByCustomerId(order1.getCustomerId())).thenReturn(Optional.of(new ArrayList<>()));
        Mockito.when(mockRestConsumerImpl.getPersonalOffer(
                IdRequest.builder().productIdList(new ArrayList<>()).build()
        )).thenReturn(expectedPersonalOfferResponse);

        PersonalOfferResponse actualPersonalOfferResponse = orderService.getPersonalOffer(order1.getCustomerId());

        assertEquals(expectedPersonalOfferResponse, actualPersonalOfferResponse);
    }

}
