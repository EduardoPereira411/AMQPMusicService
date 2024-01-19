package com.device.DeviceQueryModule.AMQP;

import com.device.DeviceQueryModule.devicemanagement.model.Device;
import com.device.DeviceQueryModule.devicemanagement.repositories.DeviceRepo.DeviceRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
public class AMQPSender {

	@Value("${rabbit.device.bootstrap}")
	String deviceBootstrapEx;

	@Value("${device.routingKey.bootstrap}")
	String bootstrapKey;

	private RabbitTemplate rabbitTemplate;

	private DeviceRepository deviceRepository;

	public AMQPSender(RabbitTemplate rabbitTemplate, DeviceRepository deviceRepository) {
		this.rabbitTemplate = rabbitTemplate;
		this.deviceRepository = deviceRepository;
	}

	public void sendGetAllDevices() {
		System.out.println(" [x] Requesting all devices");
		Iterable<LinkedHashMap<String, Object>> devicesIterable = (Iterable<LinkedHashMap<String, Object>>) rabbitTemplate.convertSendAndReceive(deviceBootstrapEx, bootstrapKey, "yes");
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
