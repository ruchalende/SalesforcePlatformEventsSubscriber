package com.example.demo.salesforce.subscriber;

import com.example.demo.salesforce.config.SalesforcePubSubProperties;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@EnableAsync
public class PlatformEventListener {

    private final PlatformEventsClient platformEventsClient;
    private final SalesforcePubSubProperties properties;

    public PlatformEventListener(PlatformEventsClient platformEventsClient,
                                 SalesforcePubSubProperties properties) {
        this.platformEventsClient = platformEventsClient;
        this.properties = properties;
    }

    @Async
    public void listen() {
        Map<String, String> events = properties.getEvent();
        for (Map.Entry<String, String> entry : events.entrySet()) {
            platformEventsClient.subscribe(entry.getKey());
        }
    }
}