package com.example.order.module.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalOfferListResponse {
    private List<PersonalOfferForEmployee> personalOfferForEmployeeList;
}