package com.example.order.module.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalOfferForEmployee {
    private Long employeeId;
    private PersonalOfferResponse personalOfferResponse;
}
