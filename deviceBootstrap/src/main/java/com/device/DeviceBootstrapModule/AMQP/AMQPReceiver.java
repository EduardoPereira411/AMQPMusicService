package com.device.DeviceBootstrapModule.AMQP;

import com.device.DeviceBootstrapModule.exceptions.NotFoundException;
import com.device.DeviceBootstrapModule.devicemanagement.model.Device;
import com.device.DeviceBootstrapModule.devicemanagement.repositories.DeviceRepository;
import com.device.DeviceBootstrapModule.devicemanagement.services.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AMQPReceiver {

	private final DeviceService deviceService;

	private final DeviceRepository deviceRepository;



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


	@RabbitListener(queues = "#{bootstrapDevicesQueue.name}")
	public Iterable<Device> giveAllDevices() {
		System.out.println("Devices sent");
		return deviceService.findAll();
	}

}
