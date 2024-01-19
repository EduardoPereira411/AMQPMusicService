package com.subscription.SubscriptionCommandModule.AMQP;

import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.*;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.repositories.PlanRepo.PlanRepository;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.repositories.SubscriptionRepo.SubscriptionRepository;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.repositories.UserRepo.UserRepository;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AMQPReceiver {

	private final PlanRepository planRepository;

	private final SubscriptionRepository subscriptionRepository;

	private final SubscriptionService subscriptionService;

	@Value("${rabbit.bonusPlan.queue}")
	String bonusPlanQueue;

	@RabbitListener(queues = "#{createdPlanQueue.name}")
	public void receiveCreatedPlan(PlanDTO plan) {
		final var isplan = planRepository.findByPlanID(plan.getPlanID());
		if(isplan.isEmpty()){
			Plan finalPlan =  new Plan(plan.getPlanID(), plan.getPlanName(), plan.getDeviceNr(), plan.isActive(), plan.isBonus());
			System.out.println("Plan " +plan.getPlanName() + " created");
			planRepository.save(finalPlan);
		}
	}

	@RabbitListener(queues = "#{updatedPlanQueue.name}")
	public void receiveUpdatedPlan(PlanDTO plan) {
		final var isplan = planRepository.findByPlanID(plan.getPlanID());
		if(isplan.isPresent()){
			final var finalPlan = isplan.get();
			finalPlan.setPlanID(plan.getPlanID());
			finalPlan.setPlanName(plan.getPlanName());
			finalPlan.setDeviceNr(plan.getDeviceNr());
			finalPlan.setActive(plan.isActive());
			planRepository.save(finalPlan);
			System.out.println("Plan " + plan.getPlanName() + " updated");
		}
	}

	@RabbitListener(queues = "#{createdSubQueue.name}")
	public void receiveCreatedSub(Subscription sub) {
		final var isSub = subscriptionRepository.findBySubID(sub.getSubID());
		if(isSub.isEmpty()){
			System.out.println("Sub " +sub.getSubID() + " created");
			subscriptionRepository.save(sub);
		}
	}

	@RabbitListener(queues = "#{updatedSubQueue.name}")
	public void receiveUpdatedSub(Subscription sub) {
		subscriptionService.updateSubAMQP(sub);
		System.out.println("Sub " + sub.getSubID() + " updated");

	}

	private final UserRepository userRepository;


	@RabbitListener(queues = "#{createdUserQueue.name}")
	public void receiveCreatedUser(UserDTO user) {
		final var isUser = userRepository.findByUserId(user.getId());
		if(isUser.isEmpty()){
			User user1 = new User(user.getId());
			System.out.println("User " +user.getUsername() + " created");
			userRepository.save(user1);
		}
	}

	@RabbitListener(queues = "bonusPlan.rpc.requests")
	public boolean receiverCreatedBonusPlan(BonusPlanRequest request) {
		return subscriptionService.createBonusSubAMQP(request);
	}

}
