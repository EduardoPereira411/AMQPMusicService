package com.plan.PlanCommandModule.AMQP;

import com.plan.PlanCommandModule.planmanagement.model.BonusPlanRequest;
import com.plan.PlanCommandModule.planmanagement.model.Plan;
import com.plan.PlanCommandModule.planmanagement.repositories.PlanRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
public class AMQPSender {

	@Value("${rabbit.plan.topic}")
	String planTopic;

	@Value("${plan.routingKey.created}")
	String createdKey;

	@Value("${plan.routingKey.updated}")
	String updatedKey;

	@Value("${rabbit.bonusPlan.rpc}")
	String bonusPlanRPC;

	@Value("${bonusPlan.routingKey.created}")
	String bonusPlanCreatedKey;


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

	public void sendCreatePlan(Plan plan) {
		rabbitTemplate.convertAndSend(planTopic,createdKey + "yes", plan);
		System.out.println(" [x] Sent plan '" + plan.getPlanName() + "'");
	}

	public void sendUpdatedPlan(Plan plan) {
		rabbitTemplate.convertAndSend(planTopic,updatedKey + "yes", plan);
		System.out.println(" [x] Sent plan '" + plan.getPlanID() + "'");
	}

	public boolean sendAskCreateBonusPlan(String userId, String planId) {
		System.out.println(" [x] Requesting");
		BonusPlanRequest rpcRequest = new BonusPlanRequest(userId,planId);
		Boolean response = (Boolean) rabbitTemplate.convertSendAndReceive(bonusPlanRPC, bonusPlanCreatedKey,rpcRequest);
		System.out.println(" [.] Got '" + response + "'");
		return !Boolean.FALSE.equals(response) && response != null;
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
			System.out.println("No bootstrapping Instance for plans is running");
		}
	}

}
