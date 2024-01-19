package com.device.DeviceBootstrapModule.devicemanagement.services;

import com.device.DeviceBootstrapModule.devicemanagement.model.Device;

import java.util.UUID;

public interface DeviceService {

    Iterable<Device> findAll();
    Device findByMac(String macAddr);
    Device updateDeviceAMQP(Device updatedDevice);

    void deleteAllFromSubAMQP (String subId);

    void deleteDeviceAMQP (String devId);
}
