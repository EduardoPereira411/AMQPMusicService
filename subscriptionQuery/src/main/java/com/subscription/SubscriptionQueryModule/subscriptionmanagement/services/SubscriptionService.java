package com.subscription.SubscriptionQueryModule.subscriptionmanagement.services;

import com.subscription.SubscriptionQueryModule.subscriptionmanagement.model.Plan;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.model.Subscription;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

public interface SubscriptionService {

    Iterable<Subscription> findAll();

    Subscription findByUUID(UUID id);

    Subscription findActiveByUser(UUID userid);

    Subscription findActiveByUserToken(UUID userid);

    Plan getPlanFromSub(UUID userid);

    void updateSubAMQP(Subscription subscription);

}
