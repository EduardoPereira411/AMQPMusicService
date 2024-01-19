package com.subscription.SubscriptionQueryModule.subscriptionmanagement.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
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

    @Version
    @Getter
    @Setter
    private long version;
    @Column(nullable = false, unique = true, updatable = true)
    @NotNull
    @NotBlank
    @Getter
    @Size(min = 1, max = 32)
    private String planName;

    @Column(nullable = false, unique = false, updatable = true)
    @NotNull
    @Getter
    @Setter
    @Min(0)
    private Integer minutes;

    @Column(nullable = false, unique = false)
    @NotNull
    @Getter
    @Setter
    @Min(0)
    private Double monthlyFee;

    @Column(nullable = false, unique = false)
    @NotNull
    @Getter
    @Setter
    @Min(0)
    private Double annualFee;

    @Column(nullable = false, unique = false, updatable = true)
    @NotNull
    @Getter
    @Setter
    @Min(0)
    private Integer deviceNr;

    @Column(nullable = false, unique = false, updatable = true)
    @NotNull
    @Getter
    @Setter
    @Min(0)
    private Integer musicCollectionsNr;


    @Column(nullable = false, unique = false, updatable = true)
    @NotNull
    @NotBlank
    @Size(min = 1, max = 32)
    private String musicRecommendation;

    @Column(nullable = false, unique = false, updatable = true)
    @NotNull
    @Getter
    @Setter
    private boolean isActive = true;

    @Column(nullable = false, unique = false, updatable = true)
    @NotNull
    @Getter
    @Setter
    private boolean isBonus = true;


    public Plan() {
        this.planID = UUID.randomUUID();
    }

    public Plan(UUID id, String planName, Integer minutes, Double monthlyFee, Double annualFee, Integer deviceNr, Integer musicCollectionsNr, String musicRecommendation, boolean isBonus) {
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

    public void update(Plan plan) {
        if (plan.getPlanName() != null) {
            setPlanName(plan.getPlanName());
        }
        if (plan.getMinutes() != null) {
            setMinutes(plan.getMinutes());
        }
        if (plan.getDeviceNr() != null) {
            setDeviceNr(plan.getDeviceNr());
        }
        if (plan.getDeviceNr() != null) {
            setMusicCollectionsNr(plan.getDeviceNr());
        }
        if (plan.getMusicRecommendation() != null) {
            setMusicRecommendation(plan.getMusicRecommendation());
        }
        if (plan.getMonthlyFee() != null) {
            setMonthlyFee(plan.getMonthlyFee());
        }
        if (plan.getAnnualFee() != null) {
            setAnnualFee(plan.getAnnualFee());
        }
    }

    //complete constructor
    public Plan(UUID id,long version, String planName, Integer minutes, Double monthlyFee, Double annualFee, Integer deviceNr, Integer musicCollectionsNr, String musicRecommendation,boolean isActive , boolean isBonus) {
        this.planID = id;
        this.version = version;
        setPlanName(planName);
        this.minutes = minutes;
        this.monthlyFee = monthlyFee;
        this.annualFee = annualFee;
        this.deviceNr = deviceNr;
        this.musicCollectionsNr = musicCollectionsNr;
        setMusicRecommendation(musicRecommendation);
        this.isActive = isActive;
        this.isBonus = isBonus;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "planID=" + planID +
                ", version=" + version +
                ", planName='" + planName + '\'' +
                ", minutes=" + minutes +
                ", monthlyFee=" + monthlyFee +
                ", annualFee=" + annualFee +
                ", deviceNr=" + deviceNr +
                ", musicCollectionsNr=" + musicCollectionsNr +
                ", musicRecommendation='" + musicRecommendation + '\'' +
                ", isActive=" + isActive +
                '}';
    }


    public void setPlanName(String planName) {
        if (planName == null || planName.isBlank()) {
            throw new IllegalArgumentException("'planName' is a mandatory attribute of the Plan");
        } else {
            this.planName = planName;
        }
    }

    public String getMusicRecommendation() {
        return musicRecommendation;
    }

    public void setMusicRecommendation(String musicRecommendation) {
        if (musicRecommendation == null || musicRecommendation.isBlank()) {
            throw new IllegalArgumentException("'musicRecommendation' is a mandatory attribute of Subscription");
        }
        if (musicRecommendation.equals("automatic") || musicRecommendation.equals("personalized")) {
            this.musicRecommendation = musicRecommendation;
        }else {
            throw new IllegalArgumentException("'musicRecommendation' must be 'automatic' or 'personalized'");
        }
    }
}
