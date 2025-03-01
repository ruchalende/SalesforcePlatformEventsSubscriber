package com.example.demo.salesforce.auth;

import com.example.demo.salesforce.config.SalesforceSecurityProperties;

import java.util.HashMap;
import java.util.Map;

public class SalesforceAuthService {

    private final SalesforceAuthClient salesforceAuthClient;
    private final SalesforceSecurityProperties salesforceSecurityProperties;

    public SalesforceAuthService(SalesforceAuthClient salesforceAuthClient,
                                 SalesforceSecurityProperties salesforceSecurityProperties) {
        this.salesforceAuthClient = salesforceAuthClient;
        this.salesforceSecurityProperties = salesforceSecurityProperties;
    }

    public SalesforceSession login() {
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", salesforceSecurityProperties.getGrantType());
        params.put("client_id", salesforceSecurityProperties.getClientID());
        params.put("client_secret", salesforceSecurityProperties.getClientSecret());
        SalesforceAuthResponse authResponse = salesforceAuthClient.authenticate(params);
        String id = authResponse.id();
        String orgID = id.substring(id.indexOf("id/") + 3, id.lastIndexOf("/"));
        return new SalesforceSession(authResponse.accessToken(), orgID, authResponse.instanceUrl());
    }
}

