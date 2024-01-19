package com.subscription.SubscriptionCommandModule.subscriptionmanagement.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import com.subscription.SubscriptionCommandModule.subscriptionmanagement.api.CreateSubscriptionRequest;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.BonusPlanRequest;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.Plan;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.Subscription;

import javax.servlet.http.HttpServletRequest;

public interface SubscriptionService {

    Iterable<Subscription> findAll();

    Subscription findByUUID(UUID id);

    Subscription findActiveByUser(UUID userid);

    Subscription create(CreateSubscriptionRequest resource, UUID userId);

    Subscription cancel(UUID userId, long desiredVersion);

    Subscription changePlanFromSub(UUID userId, long desiredVersion, String planName);

    Subscription renew(UUID userId, long desiredVersion);


    void updateSubAMQP(Subscription subscription);

    boolean createBonusSubAMQP(BonusPlanRequest request);

}
