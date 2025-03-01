package com.example.demo.salesforce.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SalesforceAuthResponse(
        @JsonProperty("access_token")
        String accessToken,
        String signature,
        @JsonProperty("instance_url")
        String instanceUrl,
        String id,
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("issued_at")
        long issuedAt
) {
}
