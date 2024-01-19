package com.subscription.SubscriptionBootstrapModule.subscriptionmanagement.repositories;


import com.subscription.SubscriptionBootstrapModule.subscriptionmanagement.model.Subscription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends CrudRepository<Subscription, UUID>{

    Optional<Subscription> findBySubID(UUID SubID);

    @Query("SELECT s FROM Subscription s WHERE s.isActive = true AND s.userId = :userID  AND s.isBonus = false ")
    Optional<Subscription> findNonBonusActiveSubscriptionByUserId(@Param("userID") UUID userID);

}
