package com.example.order.module.rest;

import com.example.order.module.model.response.EmployeeListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RestConsumerEmployeeImpl implements RestConsumerEmployee {
    private static final String EMPLOYEE = "/employee/";
    private final RestTemplate restTemplate;
    @Value("${service.host.employee}")
    private String serviceHostEmployee;

    @Override
    public EmployeeListResponse getAllEmployees() {
        String resourceUrl = serviceHostEmployee + EMPLOYEE;

        return restTemplate.getForObject(resourceUrl, EmployeeListResponse.class);
    }
}