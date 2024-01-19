package com.subscription.SubscriptionCommandModule.bootstraping;

import com.subscription.SubscriptionCommandModule.AMQP.AMQPSender;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootstrapperCaller implements CommandLineRunner {

    private final AMQPSender sender;

    private final RabbitListenerEndpointRegistry registry;


    @Override
    public void run(final String... args) throws Exception {
        sender.sendGetAllPlans();
        sender.sendGetAllUsers();
        sender.sendGetAllSubs();
        registry.start();
    }
}