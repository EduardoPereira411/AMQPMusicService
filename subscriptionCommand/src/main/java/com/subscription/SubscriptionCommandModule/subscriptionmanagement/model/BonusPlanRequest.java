package com.subscription.SubscriptionCommandModule.subscriptionmanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class BonusPlanRequest {

    @JsonProperty("userId")
    @Getter
    @Setter
    private String userId;

    @JsonProperty("planId")
    @Getter
    @Setter
    private String planId;

    public BonusPlanRequest(String userId, String planId) {
        this.userId = userId;
        this.planId = planId;
    }
}
