package com.subscription.SubscriptionQueryModule.AMQP;


import com.subscription.SubscriptionQueryModule.subscriptionmanagement.model.Plan;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.model.Subscription;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.repositories.PlanRepo.PlanRepository;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.repositories.SubscriptionRepo.SubscriptionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
public class AMQPSender {

	@Value("${rabbit.plan.bootstrap}")
	String planBootstrapEx;

	@Value("${plan.routingKey.bootstrap}")
	String planBootstrapKey;


	@Value("${rabbit.subscription.bootstrap}")
	String subBootstrapEx;

	@Value("${subscription.routingKey.bootstrap}")
	String subBootstrapKey;

	private RabbitTemplate rabbitTemplate;

	private PlanRepository planRepository;

	private SubscriptionRepository subscriptionRepository;

	public AMQPSender(RabbitTemplate rabbitTemplate, PlanRepository planRepository, SubscriptionRepository subscriptionRepository) {
		this.rabbitTemplate = rabbitTemplate;
		this.planRepository = planRepository;
		this.subscriptionRepository = subscriptionRepository;
	}

	public void sendGetAllPlans() {
		System.out.println(" [x] Requesting all plans");
		Iterable<LinkedHashMap<String, Object>> plansIterable = (Iterable<LinkedHashMap<String, Object>>) rabbitTemplate.convertSendAndReceive(planBootstrapEx, planBootstrapKey, "yes");
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

	public void sendGetAllSubs() {
		System.out.println(" [x] Requesting all subs");
		Iterable<LinkedHashMap<String, Object>> subsIterable = (Iterable<LinkedHashMap<String, Object>>) rabbitTemplate.convertSendAndReceive(subBootstrapEx, subBootstrapKey, "yes");
		System.out.println(subsIterable);
		if(subsIterable!=null) {
			List<Subscription> subsList = new ArrayList<>();
			subsIterable.forEach(linkedHashMap -> {
				Subscription subscription = new Subscription(
						UUID.fromString((String) linkedHashMap.get("subID")),
						((Number) linkedHashMap.get("version")).longValue(),
						UUID.fromString((String) linkedHashMap.get("userId")),
						UUID.fromString((String) linkedHashMap.get("planId")),
						convertToLocalDate((List<Integer>) linkedHashMap.get("startDate")),
						convertToLocalDate((List<Integer>) linkedHashMap.get("endDate")),
						convertToLocalDate((List<Integer>) linkedHashMap.get("renewalDate")),
						convertToLocalDate((List<Integer>) linkedHashMap.get("cancelDate")),
						(boolean) linkedHashMap.get("active"),
						(boolean) linkedHashMap.get("renewed"),
						(String) linkedHashMap.get("paymentMode"),
						(boolean) linkedHashMap.get("bonus")
				);
				subsList.add(subscription);
			});

			if (!subsList.isEmpty()) {

				for (Subscription sub : subsList) {
					if (!subscriptionRepository.existsById(sub.getSubID())) {
						subscriptionRepository.save(sub);
						System.out.println("Subscription " + sub.getSubID() + " saved");
					} else {
						System.out.println("Subscription " + sub.getSubID() + " already exists. Skipping save.");
					}
				}
			} else {
				System.out.println("No Subscriptions were present");
			}
		}else {
			System.out.println("No bootstrapping Instance for Subscriptions is running");
		}
	}

	private LocalDate convertToLocalDate(List<Integer> dateList) {
		if(dateList!=null) {
			return LocalDate.of(dateList.get(0), dateList.get(1), dateList.get(2));
		}
		return null;
	}

}
