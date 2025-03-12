package com.example.order.module.rest;

import com.example.order.module.model.request.IdRequest;
import com.example.order.module.model.request.PersonalOfferListRequest;
import com.example.order.module.model.response.PersonalOfferListResponse;
import com.example.order.module.model.response.PersonalOfferResponse;
import com.example.order.module.model.response.ProductEntityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ClientProductImpl implements ClientProduct {
    private final RestTemplate restTemplate;
    @Value("${service.host.product}")
    private String serviceHostProduct;
    private static final String PRODUCT = "/product/";

    @Override
    public ProductEntityResponse getProduct(Long productId) {
        String resourceUrl = serviceHostProduct + PRODUCT + productId;

        return restTemplate.getForObject(resourceUrl, ProductEntityResponse.class);
    }

    @Override
    public PersonalOfferResponse getPersonalOffer(IdRequest productIdList) {
        String resourceUrl = serviceHostProduct + PRODUCT + "personal-offer";

        return restTemplate.postForObject(resourceUrl, productIdList, PersonalOfferResponse.class);
    }

    @Override
    public PersonalOfferListResponse getPersonalOfferList(PersonalOfferListRequest personalOfferListRequest) {
        String resourceUrl = serviceHostProduct + PRODUCT + "products-po-list";

        return restTemplate.postForObject(resourceUrl, personalOfferListRequest, PersonalOfferListResponse.class);
    }
}
