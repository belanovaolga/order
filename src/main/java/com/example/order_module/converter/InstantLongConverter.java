package com.example.order_module.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;

@Converter
public class InstantLongConverter implements AttributeConverter<Instant, Long> {

    @Override
    public Long convertToDatabaseColumn(Instant instant) {
        return instant.getEpochSecond();
    }

    @Override
    public Instant convertToEntityAttribute(Long aLong) {
        return aLong == null ? null : Instant.ofEpochMilli(aLong);
    }
}
