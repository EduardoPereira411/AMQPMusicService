package com.subscription.SubscriptionCommandModule.subscriptionmanagement.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class User {

    @Id
    @Getter
    @Setter
    UUID userId;

    public User() {

    }

    public User(UUID userId) {
        this.userId = userId;
    }
}
