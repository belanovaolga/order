package com.example.order_module.kafka;

import com.example.order_module.model.request.ProductCountDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class KafkaSender {
    private final KafkaTemplate<String, ProductCountDto> kafkaTemplate;

    public void sendProductCount(ProductCountDto productCountDto) {
        kafkaTemplate.send("product-count-topic", productCountDto);
    }
}
