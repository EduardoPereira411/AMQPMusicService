package com.device.DeviceCommandModule.AMQP;

import com.device.DeviceCommandModule.devicemanagement.model.*;
import com.device.DeviceCommandModule.devicemanagement.repositories.DeviceRepo.DeviceRepository;
import com.device.DeviceCommandModule.devicemanagement.repositories.PlanRepo.PlanRepository;
import com.device.DeviceCommandModule.devicemanagement.repositories.SubscriptionRepo.SubscriptionRepository;
import com.device.DeviceCommandModule.devicemanagement.services.DeviceService;
import com.device.DeviceCommandModule.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class AMQPReceiver {

	private final DeviceService deviceService;

	private final DeviceRepository deviceRepository;

	private final SubscriptionRepository subscriptionRepository;

	private final PlanRepository planRepository;

	@RabbitListener(queues = "#{createdDeviceQueue.name}")
	public void receiveCreatedDevice(Device device) {
		try{
			deviceService.findByMac(device.getMAC());
		}catch (NotFoundException ex){
			System.out.println("Device " +device.getDesignation() + " created");
			deviceRepository.save(device);
		}
	}

	@RabbitListener(queues = "#{updatedDeviceQueue.name}")
	public void receiveUpdatedDevice(Device device) {
		deviceService.updateDeviceAMQP(device);
		System.out.println("Device " + device.getDesignation() + " updated");
	}

	@RabbitListener(queues = "#{deletedDeviceQueue.name}")
	public void receiveDeletedDevice(String device) {
		deviceService.deleteDeviceAMQP(device);
		System.out.println("Device " + device + " deleted");
	}

	@RabbitListener(queues = "#{allDeletedDeviceQueue.name}")
	public void receiveAllDeletedDevice(String sub) {
		deviceService.deleteAllFromSubAMQP(sub);
		System.out.println("All devices from " + sub + " deleted");
	}

	@RabbitListener(queues = "#{createdSubQueue.name}")
	public void receiveCreatedSub(SubscriptionDTO subscriptionDTO) {
		final var sub = subscriptionRepository.findBySubID(subscriptionDTO.getSubID());
		if(sub.isEmpty()){
			System.out.println("Subscription " + subscriptionDTO.getSubID() + " created");
			Subscription newSub = new Subscription(subscriptionDTO.getSubID(),subscriptionDTO.getPlanId(),
					subscriptionDTO.getUserId(),subscriptionDTO.isActive(),subscriptionDTO.isBonus());
			subscriptionRepository.save(newSub);
		}
	}

	@RabbitListener(queues = "#{updatedSubQueue.name}")
	public void receiveUpdatedSub(SubscriptionDTO subscriptionDTO) {
		deviceService.updateSub(subscriptionDTO);
		System.out.println("Sub " + subscriptionDTO.getUserId() + " updated");
	}

	@RabbitListener(queues = "#{createdPlanQueue.name}")
	public void receiveCreatedPlan(PlanDTO plan) {
		final var plano = planRepository.findByPlanName(plan.getPlanName());
		if (plano.isEmpty()) {
			Plan newPlan = new Plan(plan.getPlanID(), plan.getPlanName(), plan.getDeviceNr(), plan.isActive(), plan.isBonus());
			System.out.println("Plan " + plan.getPlanName() + " created");
			planRepository.save(newPlan);
		}
	}

	@RabbitListener(queues = "#{updatedPlanQueue.name}")
	public void receiveUpdatedPlan(PlanDTO plan) {
		final var plano = planRepository.findByPlanID(plan.getPlanID());
		if (plano.isPresent()) {
			plano.get().updatePlan(plan);
			System.out.println("Plan " + plan.getPlanName() + " updated");
			planRepository.save(plano.get());
		}
	}
}
