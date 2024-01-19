package com.subscription.SubscriptionCommandModule.subscriptionmanagement.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Entity
public class Plan {

    @Id
    @Getter
    @Setter
    private UUID planID;

    @Getter
    @Setter
    private String planName;

    @Getter
    @Setter
    private Integer deviceNr;

    @Getter
    @Setter
    private boolean isActive;

    @Getter
    @Setter
    private boolean isBonus;

    public Plan() {
    }

    public Plan(UUID planID, String planName, Integer deviceNr, boolean isActive, boolean isBonus) {
        this.planID = planID;
        this.planName = planName;
        this.deviceNr = deviceNr;
        this.isActive = isActive;
        this.isBonus = isBonus;
    }
}
