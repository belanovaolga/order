package com.example.order.module.model;

import com.example.order.module.converter.InstantLongConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ord")
public class OrderEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private Long number;
    @Column(name = "order_date")
    @Convert(converter = InstantLongConverter.class)
    private Instant orderDate;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "product_name")
    private String productName;
    private Double price;
    private Long count;
    private Double sum;
}
