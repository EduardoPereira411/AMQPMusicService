package com.subscription.SubscriptionCommandModule.subscriptionmanagement.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.StaleObjectStateException;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;


@Entity
public class Subscription implements Serializable {
    @Id
    @Getter
    @Setter
    private UUID subID;

    @Version
    @Getter
    @Setter
    private long version;

    @Getter
    @Setter
    private UUID userId;

    @Getter
    @Setter
    private UUID planId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Getter
    @Setter
    private LocalDate startDate = LocalDate.now();

    @Setter
    @Getter
    private LocalDate endDate;

    @Column(nullable = true, updatable = true)
    @Getter
    @Setter
    private LocalDate renewalDate;


    @Getter
    @Setter
    private LocalDate cancelDate;

    @Getter
    @Setter
    private boolean isActive = true;

    @Getter
    @Setter
    private boolean isRenewed = false;

    @Getter
    @Setter
    private String paymentMode;

    @Getter
    @Setter
    private boolean isBonus = false;

    public Subscription() {
        this.subID = UUID.randomUUID();
    }

    public Subscription(final String paymentMode , final UUID planId , final UUID userId) {
        this.subID = UUID.randomUUID();
        setPaymentMode(paymentMode);
        setPlanId(planId);
        initialSetEndDate();
        setUserId(userId);
    }

    public Subscription(final UUID planId , final UUID userId , final boolean isBonus) {
        this.subID = UUID.randomUUID();
        setPlanId(planId);
        setUserId(userId);
        this.isBonus = isBonus;
    }

    //complete constructor
    public Subscription(UUID subID, long version, UUID userId, UUID planId, LocalDate startDate, LocalDate endDate, LocalDate renewalDate, LocalDate cancelDate, boolean isActive, boolean isRenewed, String paymentMode, boolean isBonus) {
        this.subID = subID;
        this.version = version;
        this.userId = userId;
        this.planId = planId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.renewalDate = renewalDate;
        this.cancelDate = cancelDate;
        this.isActive = isActive;
        this.isRenewed = isRenewed;
        this.paymentMode = paymentMode;
        this.isBonus = isBonus;
    }

    public void initialSetEndDate() {
        if(paymentMode.equals("Monthly")) {
            this.endDate = LocalDate.now().plusMonths(1);
        } else if(paymentMode.equals("Annual")){
            this.endDate = LocalDate.now().plusYears(1);
        }
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "subID=" + subID +
                ", version=" + version +
                ", userId=" + userId +
                ", planId=" + planId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", renewalDate=" + renewalDate +
                ", cancelDate=" + cancelDate +
                ", isActive=" + isActive +
                ", isRenewed=" + isRenewed +
                ", paymentMode='" + paymentMode + '\'' +
                '}';
    }

    public void setPaymentMode(String paymentMode) {
        if(paymentMode!=null) {
            if (paymentMode.equals("Monthly") || paymentMode.equals("Annual")) {
                this.paymentMode = paymentMode;
            } else {
                throw new IllegalArgumentException("'paymentMode' must be Monthly or Annual");
            }
        }
    }



    public Long getVersion() {
        return version;
    }


    public void cancelSubscription(final long desiredVersion) {
        if (this.version != desiredVersion) {
            throw new StaleObjectStateException("Object was already modified by another user", this.subID);
        }
        setActive(false);
        setCancelDate(LocalDate.now());
    }

    public void updateData(final long desiredVersion, final Boolean isActive) {
        // check current version
        if (this.version != desiredVersion) {
            throw new StaleObjectStateException("Object was already modified by another user", this.subID);
        }
        // update data. if the request didn't contain a value for a field it will be
        // unset on this object. we do not allow to change the name attribute so we
        // simply ignore it
        setActive(false);

    }

    public void changePlan(final UUID planId, final long desiredVersion){
        if (this.version != desiredVersion) {
            throw new StaleObjectStateException("Object was already modified by another user", this.subID);
        }
        setPlanId(planId);
    }

    public void renewSub(final long desiredVersion){
        if (this.version != desiredVersion) {
            throw new StaleObjectStateException("Object was already modified by another user", this.subID);
        }
        this.renewalDate = LocalDate.now();
        if(Objects.equals(this.paymentMode, "Monthly")){
            setEndDate(this.endDate.plusMonths(1));
            setRenewed(true);
        }else {
            this.endDate = this.endDate.plusYears(1);
            this.isRenewed = true;
        }

    }

    public void update(Subscription newSubscription) {
        this.setSubID(newSubscription.getSubID());
        this.setUserId(newSubscription.getUserId());
        this.setPlanId(newSubscription.getPlanId());
        this.setStartDate(newSubscription.getStartDate());
        this.setEndDate(newSubscription.getEndDate());
        this.setRenewalDate(newSubscription.getRenewalDate());
        this.setCancelDate(newSubscription.getCancelDate());
        this.setActive(newSubscription.isActive());
        this.setRenewed(newSubscription.isRenewed());
        this.paymentMode = newSubscription.getPaymentMode();
    }
}
