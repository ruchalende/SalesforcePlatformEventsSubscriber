package com.example.demo.salesforce.config;

import com.example.demo.salesforce.subscriber.PlatformEventsClient;
import com.example.demo.salesforce.auth.SalesforceAuthClient;
import com.example.demo.salesforce.auth.SalesforceAuthService;
import com.example.demo.salesforce.auth.SalesforceSession;
import com.salesforce.eventbus.protobuf.PubSubGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SalesforceConfig {

    @Bean
    public SalesforceAuthService salesforceAuthService(SalesforceAuthClient salesforceAuthClient,
                                                       SalesforceSecurityProperties salesforceSecurityProperties) {
        return new SalesforceAuthService(salesforceAuthClient, salesforceSecurityProperties);
    }

    @Bean
    public ManagedChannel channel(SalesforceAuthService salesforceAuthService,
                                  SalesforcePubSubProperties properties) {
        SalesforceSession salesforceSession = salesforceAuthService.login();
        log.info("Salesforce grpc channel created, {}", salesforceSession);
        Metadata metadata = addSessionInfoMetadata(salesforceSession);
        log.info("connecting to {}", properties.getEndpoint());
        return ManagedChannelBuilder
                .forTarget(properties.getEndpoint())
                .intercept(MetadataUtils.newAttachHeadersInterceptor(metadata))
                .build();
    }

    @Bean
    public PubSubGrpc.PubSubStub stub(ManagedChannel channel) {
        return PubSubGrpc.newStub(channel);
    }

    @Bean
    public PubSubGrpc.PubSubBlockingStub blockingStub(ManagedChannel channel) {
        return PubSubGrpc.newBlockingStub(channel);
    }

    @Bean
    public PlatformEventsClient platformEventsClient(
            SalesforcePubSubProperties properties,
            PubSubGrpc.PubSubStub stub,
            PubSubGrpc.PubSubBlockingStub blockingStub) {
        return new PlatformEventsClient(properties, stub, blockingStub);
    }

    private Metadata addSessionInfoMetadata(SalesforceSession session) {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("accesstoken", Metadata.ASCII_STRING_MARSHALLER), session.accessToken());
        metadata.put(Metadata.Key.of("instanceurl", Metadata.ASCII_STRING_MARSHALLER), session.instanceUrl());
        metadata.put(Metadata.Key.of("tenantid", Metadata.ASCII_STRING_MARSHALLER), session.orgId());
        return metadata;
    }
}
