package com.example.demo.salesforce.subscriber;

import com.example.demo.salesforce.config.SalesforcePubSubProperties;
import com.salesforce.eventbus.protobuf.PubSubGrpc;
import com.salesforce.eventbus.protobuf.SchemaInfo;
import com.salesforce.eventbus.protobuf.SchemaRequest;
import com.salesforce.eventbus.protobuf.TopicInfo;
import com.salesforce.eventbus.protobuf.TopicRequest;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PlatformEventsClient {

    @Autowired
    private PubSubGrpc.PubSubStub stub;

    @Autowired
    private ManagedChannel channel;

    @Autowired
    private PubSubGrpc.PubSubBlockingStub blockingStub;

    private final SalesforcePubSubProperties properties;

    public PlatformEventsClient(
            SalesforcePubSubProperties properties,
            PubSubGrpc.PubSubStub stub,
            PubSubGrpc.PubSubBlockingStub blockingStub
    ) {
        this.properties = properties;
        this.stub = stub;
        this.blockingStub = blockingStub;
    }

    public void subscribe(String topic) {
        stub.subscribe(new PlatformEventConsumer(this, topic, properties));
    }

    private TopicInfo retrieveTopic(String topicName) {
        TopicRequest request = TopicRequest.newBuilder().setTopicName(topicName).build();
        try {
            return blockingStub.getTopic(request);
        } catch (StatusRuntimeException e) {
            log.error("Failed to retrieve topic {}: {}", topicName, e.getMessage(), e);
            return null;
        }
    }

    public Schema retrieveTopicSchema(String topicName) {
        TopicInfo topic = retrieveTopic(topicName);
        assert topic != null;
        SchemaRequest request = SchemaRequest.newBuilder().setSchemaId(topic.getSchemaId()).build();
        try {
            SchemaInfo response = blockingStub.getSchema(request);
            return new Schema.Parser().parse(response.getSchemaJson());
        } catch (StatusRuntimeException e) {
            log.error("Failed to retrieve schema for topic {}: {}", topic.getTopicName(), e.getMessage(), e);
            return null;
        }
    }

    @PreDestroy
    public void close() {
        if (channel != null) {
            log.info("disconnecting managed channel...");
            try {
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Failed to disconnect: {}", e.getMessage());
            }
        }
    }
}
