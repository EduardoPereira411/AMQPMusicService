package com.plan.PlanQueryModule.bootstraping;

import com.plan.PlanQueryModule.AMQP.AMQPSender;
import com.plan.PlanQueryModule.planmanagement.model.Plan;
import com.plan.PlanQueryModule.planmanagement.repositories.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanBootstrapper implements CommandLineRunner {

    private final AMQPSender sender;

    private final RabbitListenerEndpointRegistry registry;


    @Override
    public void run(final String... args) throws Exception {
        sender.sendGetAllPlans();
        registry.start();
    }
}
