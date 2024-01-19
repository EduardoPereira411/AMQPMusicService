package com.subscription.SubscriptionQueryModule.subscriptionmanagement.services;


import com.subscription.SubscriptionQueryModule.subscriptionmanagement.api.CreateSubscriptionRequest;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.model.Subscription;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public abstract class EditSubscriptionMapper {


    public abstract Subscription create(CreateSubscriptionRequest request);


}

