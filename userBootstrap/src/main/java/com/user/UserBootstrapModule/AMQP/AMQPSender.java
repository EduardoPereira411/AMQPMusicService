package com.user.UserBootstrapModule.AMQP;


import com.user.UserBootstrapModule.usermanagement.model.Role;
import com.user.UserBootstrapModule.usermanagement.model.User;
import com.user.UserBootstrapModule.usermanagement.repositories.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AMQPSender {

	@Value("${rabbit.user.bootstrap}")
	String userBootstrapEx;

	@Value("${user.routingKey.bootstrap}")
	String userBootstrapKey;

	private RabbitTemplate rabbitTemplate;

	private UserRepository userRepository;

	public AMQPSender(RabbitTemplate rabbitTemplate, UserRepository userRepository) {
		this.rabbitTemplate = rabbitTemplate;
		this.userRepository = userRepository;
	}

	public void sendGetAllUsers() {
		System.out.println(" [x] Requesting all users");
		Iterable<LinkedHashMap<String, Object>> usersIterable = (Iterable<LinkedHashMap<String, Object>>) rabbitTemplate.convertSendAndReceive(userBootstrapEx, userBootstrapKey, "yes");
		System.out.println(usersIterable);
		if(usersIterable!=null) {
			List<User> usersList = new ArrayList<>();
			usersIterable.forEach(linkedHashMap -> {
				User user = new User(
						UUID.fromString((String) linkedHashMap.get("id")),
						((Number) linkedHashMap.get("version")).longValue(),
						convertToLocalDate((List<Integer>) linkedHashMap.get("createdAt")),
						convertToLocalDate((List<Integer>) linkedHashMap.get("modifiedAt")),
						(String) linkedHashMap.get("createdBy"),
						(String) linkedHashMap.get("modifiedBy"),
						(boolean) linkedHashMap.get("enabled"),
						(String) linkedHashMap.get("username"),
						(String) linkedHashMap.get("password"),
						(String) linkedHashMap.get("fullName"),
						((List<LinkedHashMap<String, Object>>) linkedHashMap.get("authorities")).stream()
								.map(roleMap -> new Role((String) roleMap.get("roleName")))
								.collect(Collectors.toSet())
				);
				usersList.add(user);
			});

			if (!usersList.isEmpty()) {

				for (User user : usersList) {
					if (!userRepository.existsById(user.getId())) {
						userRepository.save(user);
						System.out.println("User " + user.getUsername() + " saved");
					} else {
						System.out.println("User " + user.getUsername() + " already exists. Skipping save.");
					}
				}
			} else {
				System.out.println("No Users were present");
			}
		}else {
			System.out.println("No User bootstrapping Instance is running");
		}
	}

	private LocalDate convertToLocalDate(List<Integer> dateList) {
		return LocalDate.of(dateList.get(0), dateList.get(1), dateList.get(2));
	}

}

