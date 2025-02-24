package com.example.order.module.rest;

import com.example.order.module.model.request.IdRequest;
import com.example.order.module.model.request.PersonalOfferListRequest;
import com.example.order.module.model.response.PersonalOfferListResponse;
import com.example.order.module.model.response.PersonalOfferResponse;
import com.example.order.module.model.response.ProductEntityResponse;

public interface RestConsumerProduct {
    ProductEntityResponse getProduct(Long productId);

    PersonalOfferResponse getPersonalOffer(IdRequest productIdList);
    PersonalOfferListResponse getPersonalOfferList(PersonalOfferListRequest personalOfferListRequest);
}
