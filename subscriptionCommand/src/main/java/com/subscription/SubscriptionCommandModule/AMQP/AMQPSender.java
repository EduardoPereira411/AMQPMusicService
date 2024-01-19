package com.subscription.SubscriptionCommandModule.AMQP;

import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.Plan;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.Subscription;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.User;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.repositories.PlanRepo.PlanRepository;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.repositories.SubscriptionRepo.SubscriptionRepository;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.repositories.UserRepo.UserRepository;
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

	@Value("${rabbit.sub.topic}")
	String subTopic;

	@Value("${sub.routingKey.created}")
	String createdSubKey;

	@Value("${sub.routingKey.updated}")
	String updatedSubKey;

	@Value("${rabbit.user.topic}")
	String userTopic;

	@Value("${user.routingKey.flip}")
	String flipUserKey;


	@Value("${rabbit.plan.bootstrap}")
	String planBootstrapEx;

	@Value("${plan.routingKey.bootstrap}")
	String planBootstrapKey;


	@Value("${rabbit.user.bootstrap}")
	String userBootstrapEx;

	@Value("${user.routingKey.bootstrap}")
	String userBootstrapKey;


	@Value("${rabbit.subscription.bootstrap}")
	String subBootstrapEx;

	@Value("${subscription.routingKey.bootstrap}")
	String subBootstrapKey;

	private RabbitTemplate rabbitTemplate;

	private PlanRepository planRepository;

	private UserRepository userRepository;

	private SubscriptionRepository subscriptionRepository;

	public AMQPSender(RabbitTemplate rabbitTemplate, PlanRepository planRepository, UserRepository userRepository, SubscriptionRepository subscriptionRepository) {
		this.rabbitTemplate = rabbitTemplate;
		this.planRepository = planRepository;
		this.userRepository = userRepository;
		this.subscriptionRepository = subscriptionRepository;
	}

	public void sendCreatedSub(Subscription sub) {
		rabbitTemplate.convertAndSend(subTopic,createdSubKey + "yes", sub);
		System.out.println(" [x] Sent Sub '" + sub.getSubID() + "'");
	}

	public void sendUpdatedSub(Subscription sub) {
		rabbitTemplate.convertAndSend(subTopic,updatedSubKey + "yes", sub);
		System.out.println(" [x] Sent Sub '" + sub.getSubID() + "'");
	}

	public void sendFlipUser(String userId) {
		rabbitTemplate.convertAndSend(userTopic, flipUserKey + "yes", userId);
		System.out.println(" [x] Sent User '" + userId + "'");
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
						(String) linkedHashMap.get("planName"),
						(Integer) linkedHashMap.get("deviceNr"),
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


	public void sendGetAllUsers() {
		System.out.println(" [x] Requesting all users");
		Iterable<LinkedHashMap<String, Object>> usersIterable = (Iterable<LinkedHashMap<String, Object>>) rabbitTemplate.convertSendAndReceive(userBootstrapEx, userBootstrapKey, "yes");
		System.out.println(usersIterable);
		if(usersIterable!=null) {
			List<User> usersList = new ArrayList<>();
			usersIterable.forEach(linkedHashMap -> {
				User user = new User(
						UUID.fromString((String) linkedHashMap.get("id"))
				);
				usersList.add(user);
			});

			if (!usersList.isEmpty()) {

				for (User user : usersList) {
					if (!userRepository.existsById(user.getUserId())) {
						userRepository.save(user);
						System.out.println("User " + user.getUserId() + " saved");
					} else {
						System.out.println("User " + user.getUserId() + " already exists. Skipping save.");
					}
				}
			} else {
				System.out.println("No Users were present");
			}
		}else {
			System.out.println("No User bootstrapping Instance is running");
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
