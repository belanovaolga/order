package com.example.order.module.rest;

import com.example.order.module.model.request.IdRequest;
import com.example.order.module.model.request.PersonalOfferListRequest;
import com.example.order.module.model.response.EmployeeListResponse;
import com.example.order.module.model.response.PersonalOfferListResponse;
import com.example.order.module.model.response.PersonalOfferResponse;
import com.example.order.module.model.response.ProductEntityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RestConsumerImpl implements RestConsumer {
    private final RestTemplate restTemplate;
    @Value("${service.host.product}")
    private String serviceHostProduct;
    @Value("${service.host.employee}")
    private String serviceHostEmployee;

    @Override
    public ProductEntityResponse getProduct(Long productId) {
        String resourceUrl = "http://" + serviceHostProduct + "/product/" + productId;

        return restTemplate.getForObject(resourceUrl, ProductEntityResponse.class);
    }

    @Override
    public PersonalOfferResponse getTwoProductsPO(IdRequest productIdList) {
        String resourceUrl = "http://" + serviceHostProduct + "/product/twoProductsPO";

        return restTemplate.postForObject(resourceUrl, productIdList, PersonalOfferResponse.class);
    }

    @Override
    public PersonalOfferResponse getPOForNoOrders() {
        String resourceUrl = "http://" + serviceHostProduct + "/product/poForNoOrders";

        return restTemplate.getForObject(resourceUrl, PersonalOfferResponse.class);
    }

    @Override
    public PersonalOfferListResponse getPersonalOfferList(PersonalOfferListRequest personalOfferListRequest) {
        String resourceUrl = "http://" + serviceHostProduct + "/product/productsPOList";

        return restTemplate.postForObject(resourceUrl, personalOfferListRequest, PersonalOfferListResponse.class);
    }

    @Override
    public EmployeeListResponse getAllEmployees() {
        String resourceUrl = "http://" + serviceHostEmployee + "/employee";

        return restTemplate.getForObject(resourceUrl, EmployeeListResponse.class);
    }
}
