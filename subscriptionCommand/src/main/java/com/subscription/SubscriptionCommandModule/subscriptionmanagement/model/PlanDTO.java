package com.subscription.SubscriptionCommandModule.subscriptionmanagement.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class PlanDTO {

    @Getter
    @Setter
    private UUID planID;

    @Getter
    @Setter
    private long version;

    @Getter
    @Setter
    private String planName;

    @Getter
    @Setter
    private Integer minutes;

    @Getter
    @Setter
    private Double monthlyFee;

    @Getter
    @Setter
    private Double annualFee;

    @Getter
    @Setter
    private Integer deviceNr;

    @Getter
    @Setter
    private Integer musicCollectionsNr;


    @Getter
    @Setter
    private String musicRecommendation;

    @Getter
    @Setter
    private boolean isActive = true;

    @Getter
    @Setter
    private boolean isBonus = false;


    public PlanDTO() {
        this.planID = UUID.randomUUID();
    }

    public PlanDTO(UUID id, String planName, Integer minutes, Double monthlyFee, Double annualFee, Integer deviceNr, Integer musicCollectionsNr, String musicRecommendation, boolean isBonus) {
        this.planID = id;
        setPlanName(planName);
        this.minutes = minutes;
        this.monthlyFee = monthlyFee;
        this.annualFee = annualFee;
        this.deviceNr = deviceNr;
        this.musicCollectionsNr = musicCollectionsNr;
        setMusicRecommendation(musicRecommendation);
        this.isBonus = isBonus;
    }
}
