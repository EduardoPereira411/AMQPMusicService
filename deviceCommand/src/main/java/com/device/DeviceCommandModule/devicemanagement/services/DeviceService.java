package com.device.DeviceCommandModule.devicemanagement.services;

import com.device.DeviceCommandModule.devicemanagement.api.CreateDeviceRequest;
import com.device.DeviceCommandModule.devicemanagement.api.EditDeviceRequest;
import com.device.DeviceCommandModule.devicemanagement.model.Device;
import com.device.DeviceCommandModule.devicemanagement.model.SubscriptionDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public interface DeviceService {

    boolean isValidMacAddress(String macAddress);

    Device findByMac(String macAddr);

    UUID getSubIdByUser(UUID userId);

    Device create(final CreateDeviceRequest resource, UUID userId);

    Device update(long desiredVersion,UUID userId ,UUID devID, EditDeviceRequest resource);

    int deleteByIdOnSub(UUID devID, UUID userId);

    int deleteAllFromSub(UUID userId);

    Device updateDeviceAMQP(Device updatedDevice);

    void updateSub(SubscriptionDTO subscriptionDTO);

    void deleteAllFromSubAMQP (String subId);

    void deleteDeviceAMQP (String devId);

}
