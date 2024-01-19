package com.subscription.SubscriptionCommandModule.subscriptionmanagement.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "A Subscription")
public class SubscriptionView {
    @Schema(description = "The ID of the subscription")
    private UUID subID;

    @Schema(description = "The User who subscribed")
    private UUID userId;

    @Schema(description = "The plan included in the subscription")
    private UUID planId;

    @Schema(description = "The date the user Subscribed")
    private LocalDate startDate;

    @Schema(description = "The date the Subscription Ends")
    private LocalDate endDate;

    @Schema(description = "is this subscription active or not?")
    private boolean isActive;

    @Schema(description = "The date the user canceled his Subscription")
    private LocalDate cancelDate;

    @Schema(description = "Date of last renewal")
    private LocalDate renewalDate;

    @Schema(description = "If The subscription is Renewed or not")
    private boolean isRenewed;

    @Schema(description = "Monhtly or annual")
    private String paymentMode;

    @Schema(description = "If The subscription is a Bonus or not")
    private boolean isBonus;
}
