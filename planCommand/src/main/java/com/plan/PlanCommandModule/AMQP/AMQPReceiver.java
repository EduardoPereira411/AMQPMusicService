package com.plan.PlanCommandModule.AMQP;

import com.plan.PlanCommandModule.exceptions.NotFoundException;
import com.plan.PlanCommandModule.planmanagement.api.EditPlanRequest;
import com.plan.PlanCommandModule.planmanagement.model.Plan;
import com.plan.PlanCommandModule.planmanagement.repositories.PlanRepository;
import com.plan.PlanCommandModule.planmanagement.services.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
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

}
