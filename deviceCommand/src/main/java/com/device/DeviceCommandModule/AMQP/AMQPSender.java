package com.device.DeviceCommandModule.AMQP;

import com.device.DeviceCommandModule.devicemanagement.model.Device;
import com.device.DeviceCommandModule.devicemanagement.model.Plan;
import com.device.DeviceCommandModule.devicemanagement.repositories.DeviceRepo.DeviceRepository;
import com.device.DeviceCommandModule.devicemanagement.repositories.PlanRepo.PlanRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
public class AMQPSender {

	@Value("${rabbit.device.topic}")
	String deviceTopic;

	@Value("${device.routingKey.created}")
	String createdKey;

	@Value("${device.routingKey.updated}")
	String updatedKey;

	@Value("${device.routingKey.deleted}")
	String deletedKey;

	@Value("${device.routingKey.allDeleted}")
	String allDeletedKey;


	@Value("${rabbit.plan.bootstrap}")
	String planBootstrapEx;

	@Value("${plan.routingKey.bootstrap}")
	String planBootstrapKey;

	@Value("${rabbit.device.bootstrap}")
	String deviceBootstrapEx;

	@Value("${device.routingKey.bootstrap}")
	String deviceBootstrapKey;

	private RabbitTemplate rabbitTemplate;

	private PlanRepository planRepository;

	private DeviceRepository deviceRepository;

	public AMQPSender(RabbitTemplate rabbitTemplate, PlanRepository planRepository, DeviceRepository deviceRepository) {
		this.rabbitTemplate = rabbitTemplate;
		this.planRepository = planRepository;
		this.deviceRepository = deviceRepository;
	}

	public void sendCreateDevice(Device device) {
		rabbitTemplate.convertAndSend(deviceTopic,createdKey + "yes", device);
		System.out.println(" [x] Sent device '" + device.getDesignation() + "'");
	}

	public void sendUpdatedDevice(Device device) {
		rabbitTemplate.convertAndSend(deviceTopic,updatedKey + "yes", device);
		System.out.println(" [x] Sent device '" + device.getDesignation() + "'");
	}

	public void sendDeletedDevice(String deviceId) {
		rabbitTemplate.convertAndSend(deviceTopic,deletedKey + "yes", deviceId);
		System.out.println(" [x] Sent deleted device '" + deviceId + "'");
	}

	public void sendDeletedAll(String subId) {
		rabbitTemplate.convertAndSend(deviceTopic,allDeletedKey + "yes", subId);
		System.out.println(" [x] Sent all deleted devices '" + subId + "'");
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


	public void sendGetAllDevices() {
		System.out.println(" [x] Requesting all devices");
		Iterable<LinkedHashMap<String, Object>> devicesIterable = (Iterable<LinkedHashMap<String, Object>>) rabbitTemplate.convertSendAndReceive(deviceBootstrapEx, deviceBootstrapKey, "yes");
		System.out.println(devicesIterable);
		if(devicesIterable!=null) {
			List<Device> devicesList = new ArrayList<>();
			devicesIterable.forEach(linkedHashMap -> {
				Device device = new Device(
						UUID.fromString((String) linkedHashMap.get("devID")),
						((Number) linkedHashMap.get("version")).longValue(),
						(String) linkedHashMap.get("mac"),
						(String) linkedHashMap.get("designation"),
						UUID.fromString((String) linkedHashMap.get("subscriptionUUID"))
				);
				devicesList.add(device);
			});

			if (!devicesList.isEmpty()) {

				for (Device device : devicesList) {
					if (!deviceRepository.existsById(device.getDevID())) {
						deviceRepository.save(device);
						System.out.println("Device " + device.getDevID() + " saved");
					} else {
						System.out.println("Device " + device.getDevID() + " already exists. Skipping save.");
					}
				}
			} else {
				System.out.println("No devices were present");
			}
		}else {
			System.out.println("No bootstrapping Instance for devices is running");
		}
	}

}
