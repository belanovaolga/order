package com.example.order.module.mapper;

import com.example.order.module.model.OrderEntity;
import com.example.order.module.model.request.OrderCreateRequest;
import com.example.order.module.model.response.OrderResponse;
import com.example.order.module.model.response.ProductEntityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface OrderMapper {

    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "number", expression = "java(null)")
    @Mapping(source = "orderCreateRequest.customerId", target = "customerId")
    @Mapping(source = "orderCreateRequest.productId", target = "productId")
    @Mapping(source = "orderCreateRequest.count", target = "count")
    @Mapping(source = "productEntityResponse.name", target = "productName")
    @Mapping(source = "productEntityResponse.currentPrice", target = "price")
    @Mapping(target = "sum", expression = "java(orderCreateRequest.getCount() * productEntityResponse.getCurrentPrice())")
    OrderEntity toOrderEntity(OrderCreateRequest orderCreateRequest, ProductEntityResponse productEntityResponse);

    OrderResponse toOrderResponse(OrderEntity orderEntity);

    List<OrderResponse> toOrderResponseList(List<OrderEntity> orderEntityList);
}
