package com.user.UserCommandModule.AMQP;


import com.user.UserCommandModule.exceptions.NotFoundException;
import com.user.UserCommandModule.usermanagement.model.User;
import com.user.UserCommandModule.usermanagement.repositories.UserRepository;
import com.user.UserCommandModule.usermanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AMQPReceiver {


	private final UserRepository userRepository;

	private final UserService userService;


	@RabbitListener(queues = "#{createdUserQueue.name}")
	public void receiveCreatedPlan(User user) {
		final var isUser = userRepository.findById(user.getId());
		if(isUser.isEmpty()){
			System.out.println("User " +user.getUsername() + " created");
			userRepository.save(user);
		}
	}

	@RabbitListener(queues = "#{flipUserQueue.name}")
	public void receiveFlipUser(String userID) {
		try{
			UUID userId = UUID.fromString(userID);
			userService.flipUserSubscription(userId);
			System.out.println("User " +userID + " role flipped");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/*	@RabbitListener(queues = "#{updatedUserQueue.name}")
	public void receiveUpdatedPlan(User user) {
		planService.updatePlanAMQP(plan);
		System.out.println("Plan " + plan.getPlanName() + " updated");
	}*/

}
