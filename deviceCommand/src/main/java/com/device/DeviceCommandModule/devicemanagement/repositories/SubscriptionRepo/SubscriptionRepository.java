package com.device.DeviceCommandModule.devicemanagement.repositories.SubscriptionRepo;

import com.device.DeviceCommandModule.devicemanagement.model.Subscription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends CrudRepository <Subscription,UUID>{

    @Query("SELECT s FROM Subscription s WHERE s.isActive = true AND s.userId = :userId  AND s.isBonus = false")
    Optional<Subscription> getNonBonusSubInfoByUser(@Param("userId") UUID userId);

    @Query("SELECT s FROM Subscription s WHERE s.isActive = true AND s.userId = :userId  AND s.isBonus = true")
    Optional<Subscription> getBonusSubInfoByUser(@Param("userId") UUID userId);

    Optional<Subscription> findBySubID(UUID subID);

}
