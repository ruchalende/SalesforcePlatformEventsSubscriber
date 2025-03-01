package com.example.demo.salesforce.subscriber;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ListenerInitializer implements ApplicationRunner {

    private final PlatformEventListener platformEventListener;

    public ListenerInitializer(PlatformEventListener platformEventListener) {
        this.platformEventListener = platformEventListener;
    }

    @Override
    public void run(ApplicationArguments args) {
        platformEventListener.listen();
    }
}
