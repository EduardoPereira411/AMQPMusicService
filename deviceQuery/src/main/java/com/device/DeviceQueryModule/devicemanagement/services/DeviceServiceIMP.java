package com.device.DeviceQueryModule.devicemanagement.services;

import com.device.DeviceQueryModule.devicemanagement.model.Device;
import com.device.DeviceQueryModule.devicemanagement.model.SubscriptionDTO;
import com.device.DeviceQueryModule.devicemanagement.repositories.DeviceRepo.DeviceRepository;
import com.device.DeviceQueryModule.devicemanagement.repositories.SubscriptionRepo.SubscriptionRepository;
import com.device.DeviceQueryModule.exceptions.NotFoundException;
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
    private final SubscriptionRepository subscriptionRepo;

    private static final String MAC_ADDRESS_REGEX = "^([0-9A-Za-z]{2}-){5}[0-9A-Za-z]{2}$";
    private static final Pattern MAC_ADDRESS_PATTERN = Pattern.compile(MAC_ADDRESS_REGEX);


    @Override
    public boolean isValidMacAddress(String macAddress) {
        if (macAddress == null) {
            return false;
        }

        return MAC_ADDRESS_PATTERN.matcher(macAddress).matches();
    }

    @Override
    public Iterable<Device> findAllFromSub(UUID userID){

        final var sub = subscriptionRepo.getNonBonusSubInfoByUser(userID);

        if (sub.isPresent()) {

            return deviceRepo.findAllBySubscriptionUUID(sub.get().getSubID());

        }else throw new NotFoundException("This user doesnt have an active subscription");
    }

    @Override
    public Device findByIdOnSub(final UUID devID, UUID userID){

        final var sub = subscriptionRepo.getNonBonusSubInfoByUser(userID);

        if (sub.isPresent()) {
        UUID subscriptionUUID = sub.get().getSubID();
        Optional<Device> localResult = deviceRepo.findByDevIDAndSubscriptionUUID(devID,subscriptionUUID);

        if (localResult.isPresent()) {
        return localResult.get();
            }else throw new NotFoundException("This user doesnt have any registered devices");

        }else throw new NotFoundException("This user doesnt have an active subscription");
    }

    @Override
    public Device findByMac(final String macAddr){
        Optional<Device> localResult = deviceRepo.findByMAC(macAddr);
        if (localResult.isPresent()) {
            return localResult.get();
        } else throw new NotFoundException("Not devices with this mac address");
    }

    @Override
    public int countDevicesFromSub(UUID userID){

        final var sub = subscriptionRepo.getNonBonusSubInfoByUser(userID);
        if(sub.isPresent()) {

            UUID subscriptionUUID = sub.get().getSubID();

            return deviceRepo.countDeviceBySubscriptionUUID(subscriptionUUID);
        }else {
            throw new NotFoundException("There is no sub for this user");
        }
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
    public void updateSub(SubscriptionDTO subscriptionDTO){
        final var sub = subscriptionRepo.findBySubID(subscriptionDTO.getSubID());
        if (sub.isPresent()) {
            if(!subscriptionDTO.isActive()){
                deviceRepo.deleteAllBySubscriptionUUID(subscriptionDTO.getSubID());
            }
            final var finalSub = sub.get();
            finalSub.update(subscriptionDTO.getPlanId(),subscriptionDTO.isActive());
            subscriptionRepo.save(finalSub);
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
