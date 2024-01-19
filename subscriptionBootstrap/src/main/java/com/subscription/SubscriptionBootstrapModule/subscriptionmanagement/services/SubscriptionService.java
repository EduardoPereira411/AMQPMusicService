package com.subscription.SubscriptionBootstrapModule.subscriptionmanagement.services;

import com.subscription.SubscriptionBootstrapModule.subscriptionmanagement.model.Subscription;

import java.util.UUID;

public interface SubscriptionService {

    Iterable<Subscription> findAll();

    Subscription findByUUID(UUID id);

    Subscription findActiveByUser(UUID userid);

    Subscription findActiveByUserToken(UUID userid);

    void updateSubAMQP(Subscription subscription);

}
