package com.example.order.module.rest;

import com.example.order.module.model.request.IdRequest;
import com.example.order.module.model.response.EmployeeListResponse;
import com.example.order.module.model.response.PersonalOfferResponse;
import com.example.order.module.model.response.ProductEntityResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestConsumerImpl implements RestConsumer {
    RestTemplate restTemplate;
    @Value("${service.url.product}")
    private String serviceUrlProduct;
    @Value("${service.url.twoProductsPO}")
    private String twoProductsPO;
    @Value("${service.url.poForNoOrders}")
    private String poForNoOrders;
    @Value("${service.url.employee}")
    private String serviceUrlEmployee;

    @PostConstruct
    void initialRest() {
        restTemplate = new RestTemplate();
    }

    @Override
    public ProductEntityResponse getProduct(Long productId) {
        String resourceUrl = serviceUrlProduct + productId;

        return restTemplate.getForObject(resourceUrl, ProductEntityResponse.class);
    }

    @Override
    public PersonalOfferResponse getTwoProductsPO(IdRequest productIdList) {
        String resourceUrl = serviceUrlProduct + twoProductsPO;

        return restTemplate.postForObject(resourceUrl, productIdList, PersonalOfferResponse.class);
    }

    @Override
    public PersonalOfferResponse getPOForNoOrders() {
        String resourceUrl = serviceUrlProduct + poForNoOrders;

        return restTemplate.getForObject(resourceUrl, PersonalOfferResponse.class);
    }

    @Override
    public EmployeeListResponse getAllEmployees() {
        return restTemplate.getForObject(serviceUrlEmployee, EmployeeListResponse.class);
    }
}
