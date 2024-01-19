package com.device.DeviceCommandModule.bootstraping;

import com.device.DeviceCommandModule.AMQP.AMQPSender;
import com.device.DeviceCommandModule.devicemanagement.model.Plan;
import com.device.DeviceCommandModule.devicemanagement.repositories.PlanRepo.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BootstrapperCaller implements CommandLineRunner {
    private final AMQPSender sender;

    private final RabbitListenerEndpointRegistry registry;


    @Override
    public void run(final String... args) throws Exception {
        sender.sendGetAllPlans();
        sender.sendGetAllDevices();
        registry.start();
    }
}