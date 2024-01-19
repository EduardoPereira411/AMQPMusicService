package com.device.DeviceBootstrapModule.devicemanagement.repositories;

import com.device.DeviceBootstrapModule.devicemanagement.model.Device;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends CrudRepository<Device, UUID>{
    Optional<Device> findByMAC(String macAddr);
    @Modifying
    @Query("DELETE FROM Device d WHERE d.devID = :devID")
    int deleteByDevID(UUID devID);
    @Modifying
    @Query("DELETE FROM Device d WHERE d.subscriptionUUID = :subscriptionUUID")
    int deleteAllBySubscriptionUUID(@Param("subscriptionUUID") UUID subscriptionUUID);

}
