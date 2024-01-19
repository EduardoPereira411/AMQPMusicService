package com.device.DeviceBootstrapModule.devicemanagement.services;

import com.device.DeviceBootstrapModule.devicemanagement.model.Device;
import com.device.DeviceBootstrapModule.devicemanagement.repositories.DeviceRepository;
import com.device.DeviceBootstrapModule.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DeviceServiceIMP implements DeviceService{

    private final DeviceRepository deviceRepo;

    private static final String MAC_ADDRESS_REGEX = "^([0-9A-Za-z]{2}-){5}[0-9A-Za-z]{2}$";
    private static final Pattern MAC_ADDRESS_PATTERN = Pattern.compile(MAC_ADDRESS_REGEX);

    @Override
    public Iterable<Device> findAll(){
        return deviceRepo.findAll();
    }

    @Override
    public Device findByMac(final String macAddr){
        Optional<Device> localResult = deviceRepo.findByMAC(macAddr);
        if (localResult.isPresent()) {
            return localResult.get();
        } else throw new NotFoundException("Not devices with this mac address");
    }

    @Override
    public Device updateDeviceAMQP(Device updatedDevice){
        try {
            final var device = findByMac(updatedDevice.getMAC());
            if(device.getVersion()==updatedDevice.getVersion()){

            }else {
                device.updateData(updatedDevice.getVersion()-1,updatedDevice.getDesignation());
            }
            return deviceRepo.save(device);
        } catch (NotFoundException ex) {
            throw new NotFoundException("Device was not found");
        }
    }

    @Override
    @Transactional
    public void deleteAllFromSubAMQP (String subId){
        UUID subscriptionUUID = UUID.fromString(subId);
        deviceRepo.deleteAllBySubscriptionUUID(subscriptionUUID);
    }

    @Override
    @Transactional
    public void deleteDeviceAMQP (String devId){
        UUID deviceID = UUID.fromString(devId);
        deviceRepo.deleteByDevID(deviceID);
    }
}
