package com.device.DeviceQueryModule.devicemanagement.services;

import com.device.DeviceQueryModule.devicemanagement.model.Device;
import com.device.DeviceQueryModule.devicemanagement.model.SubscriptionDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

public interface DeviceService {

    boolean isValidMacAddress(String macAddress);
    Iterable<Device> findAllFromSub(UUID userId);
    Device findByIdOnSub(UUID devID, UUID userId);
    Device findByMac(String macAddr);
    Device updateDeviceAMQP(Device updatedDevice);
    int countDevicesFromSub(UUID userId);

    void updateSub(SubscriptionDTO subscriptionDTO);

    void deleteAllFromSubAMQP (String subId);

    void deleteDeviceAMQP (String devId);
}
