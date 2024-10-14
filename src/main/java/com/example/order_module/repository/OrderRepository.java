package com.example.order_module.repository;

import com.example.order_module.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<List<OrderEntity>> findAllByCustomerId(Long customerId);
}
