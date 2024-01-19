package com.plan.PlanBootstrapModule.AMQP;

import com.plan.PlanBootstrapModule.exceptions.NotFoundException;
import com.plan.PlanBootstrapModule.planmanagement.model.Plan;
import com.plan.PlanBootstrapModule.planmanagement.repositories.PlanRepository;
import com.plan.PlanBootstrapModule.planmanagement.services.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AMQPReceiver {

	private final PlanService planService;

	private final PlanRepository planRepository;


	@RabbitListener(queues = "#{createdPlanQueue.name}")
	public void receiveCreatedPlan(Plan plan) {
		try{
			planService.findByPlanName(plan.getPlanName());
		}catch (NotFoundException ex){
			System.out.println("Plan " +plan.getPlanName() + " created");
			planRepository.save(plan);
		}
	}

	@RabbitListener(queues = "#{updatedPlanQueue.name}")
	public void receiveUpdatedPlan(Plan plan) {
		planService.updatePlanAMQP(plan);
		System.out.println("Plan " + plan.getPlanName() + " updated");
	}

	@RabbitListener(queues = "#{bootstrapPlanQueue.name}")
	public Iterable<Plan> giveAllPlans() {
		System.out.println("Plans sent");
		return planService.findAll();
	}

}
