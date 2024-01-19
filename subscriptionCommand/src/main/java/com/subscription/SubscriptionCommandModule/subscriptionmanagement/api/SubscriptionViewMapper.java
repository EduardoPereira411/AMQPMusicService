package com.subscription.SubscriptionCommandModule.subscriptionmanagement.api;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.Plan;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class SubscriptionViewMapper {

    public abstract SubscriptionView toSubscriptionView(Subscription subscription);

    public abstract Iterable<SubscriptionView> toSubscriptionView(Iterable<Subscription> subscriptions);

    public Map<String, Object> toAllSubscriptionsMap(Iterable<Subscription> subscriptions) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("subscriptions", toSubscriptionView(subscriptions));
        return response;
    }

    public abstract PlanView toPlanView(Plan plan);

    public Integer mapOptInt(final Optional<Integer> i) {
        return i.orElse(null);
    }

    public Long mapOptLong(final Optional<Long> i) {
        return i.orElse(null);
    }

    public String mapOptString(final Optional<String> i) {
        return i.orElse(null);
    }

    public UUID mapOptUUID(final Optional<UUID> i) {
        return i.orElse(null);
    }


}

