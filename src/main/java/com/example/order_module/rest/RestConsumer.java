package com.example.order_module.rest;

import com.example.order_module.model.request.IdRequest;
import com.example.order_module.model.response.PersonalOfferResponse;
import com.example.order_module.model.response.ProductEntityResponse;

public interface RestConsumer {

    ProductEntityResponse getProduct(Long productId);

    PersonalOfferResponse getTwoProductsPO(IdRequest productIdList);

    PersonalOfferResponse getPOForNoOrders();
}
