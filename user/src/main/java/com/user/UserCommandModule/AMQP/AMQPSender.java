package com.user.UserCommandModule.AMQP;


import com.user.UserCommandModule.usermanagement.model.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AMQPSender {

	@Value("${rabbit.user.topic}")
	String userTopic;

	@Value("${user.routingKey.created}")
	String createdKey;

	@Value("${user.routingKey.updated}")
	String updatedKey;

	private RabbitTemplate rabbitTemplate;

	public AMQPSender(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void sendCreateUser(User user) {
		rabbitTemplate.convertAndSend(userTopic,createdKey + "yes", user);
		System.out.println(" [x] Sent User '" + user.getUsername() + "'");
	}

	public void sendUpdatedUser(User user) {
		rabbitTemplate.convertAndSend(userTopic,updatedKey + "yes", user);
		System.out.println(" [x] Sent User '" + user.getUsername() + "'");
	}

}
