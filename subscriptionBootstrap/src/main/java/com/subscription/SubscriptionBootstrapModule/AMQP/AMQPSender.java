package com.subscription.SubscriptionBootstrapModule.AMQP;


import com.subscription.SubscriptionBootstrapModule.subscriptionmanagement.model.Subscription;
import com.subscription.SubscriptionBootstrapModule.subscriptionmanagement.repositories.SubscriptionRepository;
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

	@Value("${rabbit.subscription.bootstrap}")
	String subBootstrapEx;

	@Value("${subscription.routingKey.bootstrap}")
	String bootstrapKey;

	private RabbitTemplate rabbitTemplate;

	private SubscriptionRepository subscriptionRepository;

	public AMQPSender(RabbitTemplate rabbitTemplate, SubscriptionRepository subscriptionRepository) {
		this.rabbitTemplate = rabbitTemplate;
		this.subscriptionRepository = subscriptionRepository;
	}

	public void sendGetAllSubs() {
		System.out.println(" [x] Requesting all subs");
		Iterable<LinkedHashMap<String, Object>> subsIterable = (Iterable<LinkedHashMap<String, Object>>) rabbitTemplate.convertSendAndReceive(subBootstrapEx, bootstrapKey, "yes");
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
