package com.example.demo.salesforce.subscriber;

import com.example.demo.salesforce.config.SalesforcePubSubProperties;
import com.example.demo.salesforce.parser.EventDataParser;
import com.example.demo.salesforce.parser.SfPlatformEvent;
import com.salesforce.eventbus.protobuf.ConsumerEvent;
import com.salesforce.eventbus.protobuf.FetchRequest;
import com.salesforce.eventbus.protobuf.FetchResponse;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class PlatformEventConsumer implements ClientResponseObserver<FetchRequest, FetchResponse> {
    private final String topicName;
    private final Integer batchSize;
    private final EventDataParser parser;

    protected PlatformEventConsumer(
            PlatformEventsClient client,
            String topicKey,
            SalesforcePubSubProperties salesforcePubSubProperties
    ) {
        this.topicName = salesforcePubSubProperties.getEvent().get(topicKey);
        this.batchSize = salesforcePubSubProperties.getBatchSize();
        this.parser = new EventDataParser(client.retrieveTopicSchema(topicName));
    }

    @Override
    public void beforeStart(ClientCallStreamObserver<FetchRequest> requestStream) {
        log.info("Subscribing to topic [{}] and waiting for [{}] events", topicName, batchSize);
        requestStream.setOnReadyHandler(() -> {
            FetchRequest request = FetchRequest.newBuilder().setNumRequested(batchSize)
                    .setTopicName(topicName).build();
            requestStream.onNext(request);
        });
    }

    @Override
    public void onNext(FetchResponse fetchResponse) {
        List<ConsumerEvent> consumerEvents = fetchResponse.getEventsList();
        try {
            for (ConsumerEvent consumerEvent : consumerEvents) {
                SfPlatformEvent parsedEvent = parser.parse(consumerEvent);
                log.info("Received event [{}]", parsedEvent);
            }
        } catch (Exception e) {
            log.error("Failed to process message: {}", e.getMessage(), e);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Error occurred: {}", throwable.getMessage());
    }

    @Override
    public void onCompleted() {
        log.info("All platform events have been processed");
    }
}

