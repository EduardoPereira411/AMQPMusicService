package com.subscription.SubscriptionCommandModule.subscriptionmanagement.services;


import org.mapstruct.Mapper;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.Subscription;

import com.subscription.SubscriptionCommandModule.subscriptionmanagement.api.CreateSubscriptionRequest;


@Mapper(componentModel = "spring")
public abstract class EditSubscriptionMapper {


    public abstract Subscription create(CreateSubscriptionRequest request);


}

