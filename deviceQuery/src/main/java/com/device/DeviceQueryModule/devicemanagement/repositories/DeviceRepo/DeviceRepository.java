package com.device.DeviceQueryModule.devicemanagement.repositories.DeviceRepo;

import com.device.DeviceQueryModule.devicemanagement.model.Device;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends CrudRepository<Device, UUID>{
    Iterable<Device> findAllBySubscriptionUUID(UUID subscriptionUUID);

    Optional<Device> findByMAC(String macAddr);

    @Query("Select d FROM Device d WHERE d.subscriptionUUID = :subscriptionUUID and d.devID = :devID" )
    Optional<Device> findByDevIDAndSubscriptionUUID(UUID devID, UUID subscriptionUUID);

    int countDeviceBySubscriptionUUID(UUID subId);

    @Modifying
    @Query("DELETE FROM Device d WHERE d.subscriptionUUID = :subscriptionUUID and d.devID = :devID")
    int deleteByDevIDOnSubscription(UUID devID, UUID subscriptionUUID);

    @Modifying
    @Query("DELETE FROM Device d WHERE d.devID = :devID")
    int deleteByDevID(UUID devID);
    @Modifying
    @Query("DELETE FROM Device d WHERE d.subscriptionUUID = :subscriptionUUID")
    int deleteAllBySubscriptionUUID(@Param("subscriptionUUID") UUID subscriptionUUID);

}
