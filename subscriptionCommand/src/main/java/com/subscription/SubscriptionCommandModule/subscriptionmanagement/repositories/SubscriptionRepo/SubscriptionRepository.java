package com.subscription.SubscriptionCommandModule.subscriptionmanagement.repositories.SubscriptionRepo;


import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.Subscription;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SubscriptionRepository extends CrudRepository<Subscription, UUID>{

    Optional<Subscription> findBySubID(UUID SubID);

    @Query("SELECT s FROM Subscription s WHERE s.isActive = true AND s.userId = :userID AND s.isBonus = false")
    Optional<Subscription> findNonBonusActiveSubscriptionByUserId(@Param("userID") UUID userID);

    @Query("SELECT s FROM Subscription s WHERE s.isActive = true AND s.userId = :userID AND s.isBonus = true")
    Optional<Subscription> findBonusActiveSubscriptionByUserId(@Param("userID") UUID userID);
}
