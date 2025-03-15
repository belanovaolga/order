package com.example.order.module.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalOfferListRequest {
    private List<PersonalOfferData> personalOfferDataList;
}