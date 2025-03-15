package com.example.order.module.model;

import com.example.order_module.converter.InstantLongConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ord")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orderSeqGen")
    @SequenceGenerator(name = "orderSeqGen", sequenceName = "order_sequence_name", allocationSize = 1)
    private Long id;
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
