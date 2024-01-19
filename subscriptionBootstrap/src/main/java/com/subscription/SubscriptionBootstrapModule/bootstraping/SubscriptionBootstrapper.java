package com.subscription.SubscriptionBootstrapModule.bootstraping;

import com.subscription.SubscriptionBootstrapModule.AMQP.AMQPSender;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionBootstrapper implements CommandLineRunner {

    private final AMQPSender sender;

    private final RabbitListenerEndpointRegistry registry;


    @Override
    public void run(final String... args) throws Exception {
        sender.sendGetAllSubs();
        registry.start();
    }
}