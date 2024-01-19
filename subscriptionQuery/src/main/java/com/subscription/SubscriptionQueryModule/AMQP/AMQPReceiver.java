package com.subscription.SubscriptionQueryModule.AMQP;

import com.subscription.SubscriptionQueryModule.subscriptionmanagement.model.*;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.repositories.PlanRepo.PlanRepository;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.repositories.SubscriptionRepo.SubscriptionRepository;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AMQPReceiver {

	private final PlanRepository planRepository;

	private final SubscriptionRepository subscriptionRepository;

	private final SubscriptionService subscriptionService;


	@RabbitListener(queues = "#{createdPlanQueue.name}")
	public void receiveCreatedPlan(Plan plan) {
		final var isplan = planRepository.getPlanByPlanID(plan.getPlanID());
		if(isplan.isEmpty()){
			System.out.println("Plan " +plan.getPlanName() + " created");
			planRepository.save(plan);
		}
	}

	@RabbitListener(queues = "#{updatedPlanQueue.name}")
	public void receiveUpdatedPlan(Plan plan) {
		final var isplan = planRepository.getPlanByPlanID(plan.getPlanID());
		if(isplan.isPresent()){
			final var finalPlan = isplan.get();
			finalPlan.update(plan);
			planRepository.save(finalPlan);
			System.out.println("Plan " + plan.getPlanName() + " updated");
		}
	}

	@RabbitListener(queues = "#{createdSubQueue.name}")
	public void receiveCreatedSub(Subscription sub) {
		System.out.println(sub.toString());
		final var isSub = subscriptionRepository.findBySubID(sub.getSubID());
		if(isSub.isEmpty()){
			System.out.println("Sub " +sub.getUserId() + " created");
			subscriptionRepository.save(sub);
		}
	}

	@RabbitListener(queues = "#{updatedSubQueue.name}")
	public void receiveUpdatedSub(Subscription sub) {
		subscriptionService.updateSubAMQP(sub);
		System.out.println("Sub " + sub.getUserId() + " updated");

	}

}
