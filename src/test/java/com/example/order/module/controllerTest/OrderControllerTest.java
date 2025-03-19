package com.example.order.module.controllerTest;


import com.example.order.module.kafka.KafkaSender;
import com.example.order.module.model.OrderEntity;
import com.example.order.module.model.request.OrderCreateRequest;
import com.example.order.module.model.request.OrderUpdateRequest;
import com.example.order.module.model.request.ProductCountDto;
import com.example.order.module.model.response.ProductEntityResponse;
import com.example.order.module.repository.OrderRepository;
import com.example.order.module.rest.ClientProduct;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends ControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final EasyRandom generator;
    @MockBean
    private ClientProduct clientProduct;
    @Autowired
    private OrderRepository orderRepository;
    @MockBean
    private KafkaSender kafkaSender;

    OrderControllerTest() {
        this.generator = new EasyRandom();
    }

    @BeforeEach
    public void cleanDataBase() {
        orderRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void shouldCreateOrder() {
        OrderEntity orderEntity = generator.nextObject(OrderEntity.class);
        OrderCreateRequest orderCreateRequest = mergeToOrderCreateRequest(orderEntity);
        ProductEntityResponse productEntityResponse = mergeToProductEntityResponse(orderEntity);
        Mockito.when(clientProduct.getProduct(productEntityResponse.getId())).thenReturn(productEntityResponse);
        Mockito.doNothing().when(kafkaSender).sendProductCount(mergeToProductCountDto(orderEntity));

        mockMvc.perform(MockMvcRequestBuilders.post("/order")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(orderEntity.getCustomerId()))
                .andExpect(jsonPath("$.productId").value(orderEntity.getProductId()))
                .andExpect(jsonPath("$.productName").value(orderEntity.getProductName()))
                .andExpect(jsonPath("$.price").value(orderEntity.getPrice()))
                .andExpect(jsonPath("$.count").value(orderEntity.getCount()))
                .andExpect(jsonPath("$.sum").value(orderEntity.getPrice() * orderEntity.getCount()));
    }

    @Test
    @SneakyThrows
    void shouldUpdateOrder() {
        OrderEntity orderEntity = generator.nextObject(OrderEntity.class);
        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);

        UUID orderId = savedOrderEntity.getId();
        OrderEntity updatedOrderEntity = generator.nextObject(OrderEntity.class);
        updatedOrderEntity.setId(orderId);
        updatedOrderEntity.setCustomerId(orderEntity.getCustomerId());
        OrderUpdateRequest orderUpdateRequest = OrderUpdateRequest.builder()
                .productId(updatedOrderEntity.getProductId())
                .count(updatedOrderEntity.getCount())
                .build();
        ProductEntityResponse productEntityResponse = mergeToProductEntityResponse(updatedOrderEntity);
        Mockito.when(clientProduct.getProduct(orderUpdateRequest.getProductId())).thenReturn(productEntityResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/order/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value(updatedOrderEntity.getCustomerId()))
                .andExpect(jsonPath("$.productId").value(updatedOrderEntity.getProductId()))
                .andExpect(jsonPath("$.productName").value(updatedOrderEntity.getProductName()))
                .andExpect(jsonPath("$.price").value(updatedOrderEntity.getPrice()))
                .andExpect(jsonPath("$.count").value(updatedOrderEntity.getCount()))
                .andExpect(jsonPath("$.sum").value(updatedOrderEntity.getCount() * updatedOrderEntity.getPrice()));
    }

    @Test
    @SneakyThrows
    void shouldDeleteOrder() {
        OrderEntity orderEntity = generator.nextObject(OrderEntity.class);
        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);
        UUID orderId = savedOrderEntity.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/order/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void shouldDeleteOrder_whenOrderNotFound() {
        UUID orderId = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.delete("/order/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void shouldOrdersList() {
        Long customerId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/order/{customerId}/list", customerId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void shouldPersonalOffer() {
        Long customerId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/order/{customerId}/personal-offer", customerId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    private OrderCreateRequest mergeToOrderCreateRequest(OrderEntity orderEntity) {
        return OrderCreateRequest.builder()
                .customerId(orderEntity.getCustomerId())
                .productId(orderEntity.getProductId())
                .count(orderEntity.getCount())
                .build();
    }

    private ProductEntityResponse mergeToProductEntityResponse(OrderEntity orderEntity) {
        return ProductEntityResponse.builder()
                .id(orderEntity.getProductId())
                .name(orderEntity.getProductName())
                .description("")
                .count(orderEntity.getCount())
                .currentPrice(orderEntity.getPrice())
                .build();
    }

    private ProductCountDto mergeToProductCountDto(OrderEntity orderEntity) {
        return ProductCountDto.builder()
                .productId(orderEntity.getProductId())
                .count(orderEntity.getCount())
                .deleteProduct(true)
                .build();
    }
}
