package com.device.DeviceCommandModule.devicemanagement.services;

import com.device.DeviceCommandModule.AMQP.AMQPSender;
import com.device.DeviceCommandModule.devicemanagement.api.CreateDeviceRequest;
import com.device.DeviceCommandModule.devicemanagement.api.EditDeviceRequest;
import com.device.DeviceCommandModule.devicemanagement.model.Device;
import com.device.DeviceCommandModule.devicemanagement.model.SubscriptionDTO;
import com.device.DeviceCommandModule.devicemanagement.repositories.DeviceRepo.DeviceRepository;
import com.device.DeviceCommandModule.devicemanagement.repositories.PlanRepo.PlanRepository;
import com.device.DeviceCommandModule.devicemanagement.repositories.SubscriptionRepo.SubscriptionRepository;
import com.device.DeviceCommandModule.exceptions.ConflictException;
import com.device.DeviceCommandModule.exceptions.NotFoundException;
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
    private final PlanRepository planRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final EditDeviceMapper DeviceEditMapper;

    private final AMQPSender sender;
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
    public Device findByMac(final String macAddr){
        Optional<Device> localResult = deviceRepo.findByMac(macAddr);
        if (localResult.isPresent()) {
            return localResult.get();
        } else throw new NotFoundException("Not devices with this mac address");
    }

    public UUID getSubIdByUser(UUID userId){
        final var sub = subscriptionRepo.getNonBonusSubInfoByUser(userId);
        if(sub.isPresent()){
            return sub.get().getSubID();
        }
        throw new NotFoundException("This user has no active subscription");
    }

    @Override
    public Device create(final CreateDeviceRequest resource , UUID userId){

        final var sub = subscriptionRepo.getNonBonusSubInfoByUser(userId);

        if(sub.isPresent()){
            int maxDeviceNrAllowed = planRepo.getDeviceNrAllowedByPlan(sub.get().getPlanId());
            int NrOfDevices = deviceRepo.countDeviceBySubscriptionUUID(sub.get().getSubID());

            if(NrOfDevices < maxDeviceNrAllowed){
                final var device = DeviceEditMapper.create(resource);
                device.setMAC(resource.getMAC());
                device.setDesignation(resource.getDesignation());
                device.setSubscriptionUUID(sub.get().getSubID());

                    final var dev = deviceRepo.findByMac(device.getMAC());
                    if(dev.isPresent()) {
                        throw new ConflictException("This Device is already associated with a subscription");
                    }else {
                        Device createdDevice = deviceRepo.save(device);
                        sender.sendCreateDevice(createdDevice);
                        return createdDevice;
                    }
            }else throw new ConflictException("You already have the max number of devices for this plan");

        }else throw new NotFoundException("this user doesnt have an active subscription");
    }

    @Override
    public Device update(long desiredVersion,UUID userId,final UUID devID, final EditDeviceRequest resource){

        final var sub = subscriptionRepo.getNonBonusSubInfoByUser(userId);

        if (sub.isPresent()) {
            Optional<Device> localResult = deviceRepo.findByDevIDAndSubscriptionUUID(devID, sub.get().getSubID());

            if (localResult.isPresent()) {
                Device finalDev = localResult.get();
                finalDev.updateData(desiredVersion, resource.getDesignation());
                Device updatedDevice = deviceRepo.save(finalDev);
                sender.sendUpdatedDevice(updatedDevice);
                return updatedDevice;
            }else throw new NotFoundException("Device doesnt exist");
        }else throw new NotFoundException("this user doesnt have an active subscription");
    }


    @Override
    @Transactional
    public int deleteByIdOnSub(final UUID devID, UUID userId){

        final var sub = subscriptionRepo.getNonBonusSubInfoByUser(userId);
        if (sub.isPresent()) {
            UUID subscriptionUUID = sub.get().getSubID();

            int nrDeletedDevs = deviceRepo.deleteByDevIDOnSubscription(devID, subscriptionUUID);
            sender.sendDeletedDevice(String.valueOf(devID));
            return nrDeletedDevs;
        }else throw new NotFoundException("This user doesnt have an active subscription");
        }


    @Override
    @Transactional
    public int deleteAllFromSub (UUID userId){

        final var sub = subscriptionRepo.getNonBonusSubInfoByUser(userId);
        if (sub.isPresent()) {
            UUID subscriptionUUID = sub.get().getSubID();

            int nrDeletedDevs = deviceRepo.deleteAllBySubscriptionUUID(subscriptionUUID);
            sender.sendDeletedAll(getSubIdByUser(userId).toString());
            return nrDeletedDevs;
        }else throw new NotFoundException("This user doesnt have an active subscription");
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
            throw new NotFoundException("Plan was not found");
        }
    }

    @Override
    @Transactional
    public void updateSub(SubscriptionDTO subscriptionDTO){
        final var sub = subscriptionRepo.findBySubID(subscriptionDTO.getSubID());
        if (sub.isPresent()) {
            if(!subscriptionDTO.isActive()){
                deviceRepo.deleteAllBySubscriptionUUID(subscriptionDTO.getSubID());
            }else{
                int maxDeviceNrAllowed = planRepo.getDeviceNrAllowedByPlan(subscriptionDTO.getPlanId());
                int NrOfDevices = deviceRepo.countDeviceBySubscriptionUUID(subscriptionDTO.getSubID());

                if(NrOfDevices > maxDeviceNrAllowed){
                    sender.sendDeletedAll(subscriptionDTO.getSubID().toString());
                }
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
