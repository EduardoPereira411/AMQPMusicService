package com.device.DeviceQueryModule.devicemanagement.model;

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

@Entity
public class Subscription implements Serializable {
    @Id
    @Getter
    @Setter
    private UUID subID;

    @Getter
    @Setter
    private UUID userId;

    @Getter
    @Setter
    private UUID planId;

    @Getter
    @Setter
    private boolean isActive = true;

    @Getter
    @Setter
    private boolean isBonus = false;

    public Subscription() {
    }

    public Subscription(final UUID subID, final UUID planId, final UUID userId, final boolean isActive, final boolean isBonus) {
        setSubID(subID);
        setPlanId(planId);
        setUserId(userId);
        setActive(isActive);
        setBonus(isBonus);
    }

    public void update(final UUID planId,final boolean isActive){
        setPlanId(planId);
        setActive(isActive);
    }
}
