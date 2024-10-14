package com.example.order_module.rest;

import com.example.order_module.model.request.IdRequest;
import com.example.order_module.model.response.PersonalOfferResponse;
import com.example.order_module.model.response.ProductEntityResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Component
public class RestConsumerImpl implements RestConsumer {
    RestTemplate restTemplate = new RestTemplate();

    @Value("${service.url.product}")
    private String serviceUrl;

    @Value("${service.url.twoProductsPO}")
    private String twoProductsPO;

    @Value("${service.url.poForNoOrders}")
    private String poForNoOrders;

    @Override
    public ProductEntityResponse getProduct(Long productId) {
        String resourceUrl = serviceUrl + productId;

        return restTemplate.getForObject(resourceUrl, ProductEntityResponse.class);
    }

    @Override
    public PersonalOfferResponse getTwoProductsPO(IdRequest productIdList) {
        String resourceUrl = serviceUrl + twoProductsPO;

        return restTemplate.postForObject(resourceUrl, productIdList, PersonalOfferResponse.class);
    }

    @Override
    public PersonalOfferResponse getPOForNoOrders() {
        String resourceUrl = serviceUrl + poForNoOrders;

        return restTemplate.getForObject(resourceUrl, PersonalOfferResponse.class);
    }
}
