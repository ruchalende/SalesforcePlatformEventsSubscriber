package com.example.demo.salesforce.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Service
@FeignClient(name = "sfAuth")
public interface SalesforceAuthClient {

    @PostMapping(
            value = "/services/oauth2/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    SalesforceAuthResponse authenticate(Map<String, ?> authForm);
}
