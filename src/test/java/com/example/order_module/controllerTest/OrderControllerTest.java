package com.example.order_module.controllerTest;

import com.example.order_module.model.request.OrderCreateRequest;
import com.example.order_module.model.request.OrderUpdateRequest;
import com.example.order_module.model.response.ProductEntityResponse;
import com.example.order_module.rest.RestConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrderControllerTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RestConsumer restConsumer;

    @Test
    @SneakyThrows
    void shouldCreateOrder() {
        OrderCreateRequest orderCreateRequest = OrderCreateRequest.builder().customerId(1L).productId(1L).count(2L).build();
        ProductEntityResponse productEntityResponse = ProductEntityResponse.builder().id(1L).name("apple").description("gold apple").count(25L).currentPrice(104.99).build();
        Mockito.when(restConsumer.getProduct(1L)).thenReturn(productEntityResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/order")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderCreateRequest)))
                .andExpect(status().isOk())
//              нужно МЕНЯТЬ ЦИФРУ при каждом последующем запуске теста
                .andExpect(jsonPath("$.id").value(19))
//                .andExpect(jsonPath("$.number").value(null))
//                .andExpect(jsonPath("$.orderDate").value())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.productName").value("apple"))
                .andExpect(jsonPath("$.price").value(104.99))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.sum").value(209.98));
    }

    @Test
    @SneakyThrows
    void shouldUpdateOrder() {
//      нужно МЕНЯТЬ ЦИФРУ при каждом последующем запуске теста
        Long orderId = 13L;
        OrderUpdateRequest orderUpdateRequest = OrderUpdateRequest.builder().productId(2L).count(2L).build();
        ProductEntityResponse productEntityResponse = ProductEntityResponse.builder().id(2L).name("apple").description("gold apple").count(25L).currentPrice(104.99).build();
        Mockito.when(restConsumer.getProduct(2L)).thenReturn(productEntityResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("http://localhost:8080/order/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderUpdateRequest)))
                .andExpect(status().isOk())
//              нужно МЕНЯТЬ ЦИФРУ при каждом последующем запуске теста
                .andExpect(jsonPath("$.id").value(14))
//                .andExpect(jsonPath("$.number").value(null))
//                .andExpect(jsonPath("$.orderDate").value())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.productId").value(2))
                .andExpect(jsonPath("$.productName").value("apple"))
                .andExpect(jsonPath("$.price").value(104.99))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.sum").value(209.98));
    }

    @Test
    @SneakyThrows
    void shouldDeleteOrder() {
//      нужно МЕНЯТЬ ЦИФРУ при каждом последующем запуске теста
        Long orderId = 15L;

        mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:8080/order/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void shouldDeleteOrder_whenOrderNotFound() {
        Long orderId = 15L;

        mockMvc.perform(MockMvcRequestBuilders.delete("http://localhost:8080/order/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void shouldOrdersList() {
        Long customerId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/order/{customerId}/list", customerId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void shouldPersonalOffer() {
        Long customerId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/order/{customerId}/personal-offer", customerId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }
}
