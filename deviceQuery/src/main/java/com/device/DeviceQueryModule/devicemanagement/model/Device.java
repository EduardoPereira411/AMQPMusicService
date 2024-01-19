package com.device.DeviceQueryModule.devicemanagement.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.StaleObjectStateException;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Entity
public class Device {
    @Id
    @Getter
    @Setter
    private UUID devID;

    @Version
    @Getter
    @Setter
    private long version;
    @Column(nullable = false, unique = true)
    @NotNull
    @NotBlank
    @Getter
    @Size(max = 17)
    private String MAC;

    @Column(nullable = false)
    @NotNull
    @NotBlank
    @Getter
    @Size(min = 1, max = 50)
    private String designation;
    @Column(nullable = false)
    @NotNull
    @Getter
    private UUID subscriptionUUID;


    public Device() {
        this.devID = UUID.randomUUID();
    }
    public Device(final String MAC, final String designation, final UUID subscriptionUUID ) {
        this.devID = UUID.randomUUID();
        setSubscriptionUUID(subscriptionUUID);
        setMAC(MAC);
        setDesignation(designation);
    }

    public Device(final UUID devID, final String MAC, final String designation, final UUID subscriptionUUID ) {
        setDevID(devID);
        setSubscriptionUUID(subscriptionUUID);
        setMAC(MAC);
        setDesignation(designation);
    }

    //complete constructor
    public Device(final UUID devID,final long version, final String MAC, final String designation, final UUID subscriptionUUID ) {
        setDevID(devID);
        this.version=version;
        setSubscriptionUUID(subscriptionUUID);
        setMAC(MAC);
        setDesignation(designation);
    }

    public void setSubscriptionUUID(UUID subscriptionUUID) {
        if (subscriptionUUID == null ) {
            throw new IllegalArgumentException("'subscriptionUUID' is a mandatory attribute of Device");
        }
        this.subscriptionUUID = subscriptionUUID;
    }

    public void setMAC(final String MAC) {
        if (MAC == null || MAC.isBlank()) {
            throw new IllegalArgumentException("'MAC' is a mandatory attribute of Device");
        }
        if (!MAC.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Invalid character(s) in 'MAC', i.e., only alphanumeric are valid");
        }
        this.MAC = MAC;
    }

    public void setDesignation(final String designation) {
        if (designation == null || designation.isBlank()) {
            throw new IllegalArgumentException("'designation' is a mandatory attribute of Device");
        }
        this.designation = designation;
    }

    public void updateData(final long desiredVersion, final String designation) {
        // check current version
        if (this.version != desiredVersion) {
            throw new StaleObjectStateException("Object was already modified by another user", this.devID);
        }
        if (designation!=null){
            setDesignation(designation);
        }
    }
}
