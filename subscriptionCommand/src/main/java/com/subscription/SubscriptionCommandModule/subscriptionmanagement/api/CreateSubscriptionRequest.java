package com.subscription.SubscriptionCommandModule.subscriptionmanagement.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubscriptionRequest{

    private String paymentMode;

    @NotNull
    @NotBlank
    private String planName;

    private UUID userId;

}
