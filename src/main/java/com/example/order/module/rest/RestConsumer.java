package com.example.order.module.rest;

import com.example.order.module.model.request.IdRequest;
import com.example.order.module.model.response.EmployeeListResponse;
import com.example.order.module.model.response.PersonalOfferResponse;
import com.example.order.module.model.response.ProductEntityResponse;

public interface RestConsumer {
    ProductEntityResponse getProduct(Long productId);
    PersonalOfferResponse getTwoProductsPO(IdRequest productIdList);
    PersonalOfferResponse getPOForNoOrders();

    EmployeeListResponse getAllEmployees();
}
