package com.user.UserBootstrapModule.AMQP;


import com.user.UserBootstrapModule.usermanagement.model.User;
import com.user.UserBootstrapModule.usermanagement.repositories.UserRepository;
import com.user.UserBootstrapModule.usermanagement.services.UserService;
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

	@RabbitListener(queues = "#{bootstrapUserQueue.name}")
	public Iterable<User> sendAllUsers() {
		System.out.println("Sending Users");
		return userRepository.findAll();
	}

}
