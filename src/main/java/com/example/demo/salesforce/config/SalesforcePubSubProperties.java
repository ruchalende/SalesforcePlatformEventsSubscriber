package com.example.demo.salesforce.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "salesforce.pubsub")
@Component
@Data
public class SalesforcePubSubProperties {
    private String endpoint;
    private Integer batchSize;
    private Map<String, String> event;
}

