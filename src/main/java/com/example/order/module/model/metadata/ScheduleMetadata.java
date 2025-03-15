package com.example.order.module.model.metadata;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class ScheduleMetadata {
    private String username;
    private String toEmail;
    private String subject;
}