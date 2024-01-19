package com.plan.PlanBootstrapModule.bootstraping;

import com.plan.PlanBootstrapModule.AMQP.AMQPSender;
import com.plan.PlanBootstrapModule.planmanagement.model.Plan;
import com.plan.PlanBootstrapModule.planmanagement.repositories.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanBootstrapper implements CommandLineRunner {
    private final PlanRepository planRepo;

    private final AMQPSender sender;

    private final RabbitListenerEndpointRegistry registry;


    @Override
    public void run(final String... args) throws Exception {
        sender.sendGetAllPlans();

        if(planRepo.count()==0) {
            if (planRepo.findByPlanID(UUID.fromString("a91c5e04-286b-43c7-a432-5b1a625f0af9")).isEmpty()) {
                final Plan free = new Plan(UUID.fromString("a91c5e04-286b-43c7-a432-5b1a625f0af9"), "Free", 1000, 0.00, 0.00, 1, 0, "automatic", false);
                planRepo.save(free);
            }
            if (planRepo.findByPlanID(UUID.fromString("31a6629f-db02-4c5f-9d8f-a1ada1f4f0c4")).isEmpty()) {
                final Plan silver = new Plan(UUID.fromString("31a6629f-db02-4c5f-9d8f-a1ada1f4f0c4"), "Silver", 5000, 4.99, 49.90, 3, 10, "automatic", false);
                planRepo.save(silver);
            }
            if (planRepo.findByPlanID(UUID.fromString("2ac26883-c050-4d90-83fa-a53cf033b77c")).isEmpty()) {
                final Plan gold = new Plan(UUID.fromString("2ac26883-c050-4d90-83fa-a53cf033b77c"), "Gold", Integer.MAX_VALUE, 5.99, 59.90, 10, 25, "personalized", false);
                planRepo.save(gold);
            }
        }

        registry.start();
    }
}