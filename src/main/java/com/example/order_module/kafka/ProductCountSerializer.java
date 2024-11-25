package com.example.order_module.kafka;

import com.example.order_module.model.request.ProductCountDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class ProductCountSerializer implements Serializer<ProductCountDto> {

    private final ObjectMapper objectMapper;

    public ProductCountSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public byte[] serialize(String topic, ProductCountDto productCountDto) {
        try {
            if (productCountDto == null) {
                return new byte[0];
            }
            return objectMapper.writeValueAsBytes(productCountDto);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing ProductCountDto to byte[]");
        }
    }
}
