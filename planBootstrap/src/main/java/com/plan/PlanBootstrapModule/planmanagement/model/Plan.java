package com.plan.PlanBootstrapModule.planmanagement.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.StaleObjectStateException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

//import org.hibernate.StaleObjectStateException;

@Entity
public class Plan {


    @Id
    @Getter
    @Setter
    private UUID planID;


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
    private boolean isBonus = false;


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

    public void setPlanName(String planName) {
        if (planName == null || planName.isBlank()) {
            throw new IllegalArgumentException("'planName' is a mandatory attribute of Subscription");
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

    public void changePlanDetails(final long desiredVersion, final String planName, final Integer minutes, final Integer deviceNr,
                                  final Integer musicCollectionsNr, final String musicRecommendation, final Double monthlyFee, final Double annualFee) {
        // check current version
        if (this.version != desiredVersion) {
            throw new StaleObjectStateException("Object was already modified by another user", this.planID);
        }

        if (planName != null) {
            setPlanName(planName);
        }
        if (minutes != null) {
            setMinutes(minutes);
        }
        if (deviceNr != null) {
            setDeviceNr(deviceNr);
        }
        if (musicCollectionsNr != null) {
            setMusicCollectionsNr(musicCollectionsNr);
        }
        if(musicRecommendation!= null){
            setMusicRecommendation(musicRecommendation);
        }
        if(monthlyFee!=null){
            setMonthlyFee(monthlyFee);
        }
        if(annualFee!=null){
            setAnnualFee(annualFee);
        }
    }


    public void updatePlan(Plan newPlan) {
        this.setPlanName(newPlan.getPlanName());
        this.setVersion(newPlan.getVersion());
        this.setMinutes(newPlan.getMinutes());
        this.setMonthlyFee(newPlan.getMonthlyFee());
        this.setAnnualFee(newPlan.getAnnualFee());
        this.setDeviceNr(newPlan.getDeviceNr());
        this.setMusicCollectionsNr(newPlan.getMusicCollectionsNr());
        this.setMusicRecommendation(newPlan.getMusicRecommendation());
        this.setActive(newPlan.isActive());
        this.setBonus(newPlan.isBonus());
    }
}



