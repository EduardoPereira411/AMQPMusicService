package com.plan.PlanBootstrapModule.AMQP;

import com.plan.PlanBootstrapModule.planmanagement.model.Plan;
import com.plan.PlanBootstrapModule.planmanagement.repositories.PlanRepository;
import com.plan.PlanBootstrapModule.planmanagement.services.PlanService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
public class AMQPSender {

	@Value("${rabbit.plan.bootstrap}")
	String planBootstrapEx;

	@Value("${plan.routingKey.bootstrap}")
	String bootstrapKey;

	private RabbitTemplate rabbitTemplate;

	private PlanRepository planRepository;

	public AMQPSender(RabbitTemplate rabbitTemplate, PlanRepository planRepository) {
		this.rabbitTemplate = rabbitTemplate;
		this.planRepository = planRepository;
	}

	public void sendGetAllPlans() {
		System.out.println(" [x] Requesting all plans");
		Iterable<LinkedHashMap<String, Object>> plansIterable = (Iterable<LinkedHashMap<String, Object>>) rabbitTemplate.convertSendAndReceive(planBootstrapEx, bootstrapKey, "yes");
		System.out.println(plansIterable);
		if(plansIterable!=null) {
			List<Plan> plansList = new ArrayList<>();
			plansIterable.forEach(linkedHashMap -> {
				Plan plan = new Plan(
						UUID.fromString((String) linkedHashMap.get("planID")),
						((Number) linkedHashMap.get("version")).longValue(),
						(String) linkedHashMap.get("planName"),
						(Integer) linkedHashMap.get("minutes"),
						(Double) linkedHashMap.get("monthlyFee"),
						(Double) linkedHashMap.get("annualFee"),
						(Integer) linkedHashMap.get("deviceNr"),
						(Integer) linkedHashMap.get("musicCollectionsNr"),
						(String) linkedHashMap.get("musicRecommendation"),
						(boolean) linkedHashMap.get("active"),
						(boolean) linkedHashMap.get("bonus")
				);
				plansList.add(plan);
			});

			if (!plansList.isEmpty()) {

				for (Plan plan : plansList) {
					if (!planRepository.existsById(plan.getPlanID())) {
						planRepository.save(plan);
						System.out.println("Plan " + plan.getPlanName() + " saved");
					} else {
						System.out.println("Plan " + plan.getPlanName() + " already exists. Skipping save.");
					}
				}
			} else {
				System.out.println("No plans were present");
			}
		}else {
			System.out.println("No bootstrapping Instance for plans is running. I am the First one !!!");
		}
	}

}
