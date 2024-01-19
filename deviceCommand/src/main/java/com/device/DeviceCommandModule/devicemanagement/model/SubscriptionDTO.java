package com.device.DeviceCommandModule.devicemanagement.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;
public class SubscriptionDTO implements Serializable {
    @Getter
    @Setter
    private UUID subID;

    @Getter
    @Setter
    private long version;

    @Getter
    @Setter
    private UUID userId;

    @Getter
    @Setter
    private UUID planId;

    @Getter
    @Setter
    private LocalDate startDate;

    @Setter
    @Getter
    private LocalDate endDate;

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

    public SubscriptionDTO() {
        this.subID = UUID.randomUUID();
    }

    public SubscriptionDTO(final UUID subID, final String paymentMode , final UUID planId , final UUID userId) {
        this.subID = subID;
        setPaymentMode(paymentMode);
        setPlanId(planId);
        setUserId(userId);
    }
}
