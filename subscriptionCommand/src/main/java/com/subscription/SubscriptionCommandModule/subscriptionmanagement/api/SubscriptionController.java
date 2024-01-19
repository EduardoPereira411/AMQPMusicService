package com.subscription.SubscriptionCommandModule.subscriptionmanagement.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.subscription.SubscriptionCommandModule.AMQP.AMQPSender;
import com.subscription.SubscriptionCommandModule.configuration.KeyDecoder;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.Role;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.services.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@Tag(name = "Subscription", description = "Endpoints for managing Subscriptions")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionController {
    private final SubscriptionService service;
    private final SubscriptionViewMapper subscriptionViewMapper;
    private final KeyDecoder decoder;

    private Long getVersionFromIfMatchHeader(final String ifMatchHeader) {
        if (ifMatchHeader == null || ifMatchHeader.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must issue a conditional PATCH using 'if-match'");
        }
        if (ifMatchHeader.startsWith("\"")) {
            return Long.parseLong(ifMatchHeader.substring(1, ifMatchHeader.length() - 1));
        }
        return Long.parseLong(ifMatchHeader);
    }

    @Operation(summary = "Creates a new Subscription")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed(Role.NEW_USER)
    public ResponseEntity<SubscriptionView> create(HttpServletRequest request ,@Valid @RequestBody final CreateSubscriptionRequest resource){
        UUID userid = decoder.getIdbyToken(request);

        final var subscription = service.create(resource, userid);
        final var newsubUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(subscription.getSubID().toString())
                .build().toUri();

        return ResponseEntity.created(newsubUri).eTag(Long.toString(subscription.getVersion()))
                .body(subscriptionViewMapper.toSubscriptionView(subscription));
    }

    @Operation(summary = "Changed the plan of the logged in user")
    @PatchMapping(value = "/plan/{planName}")
    @RolesAllowed(Role.SUBSCRIBER)
    public ResponseEntity<SubscriptionView> changePlan(HttpServletRequest request,
                                                       @PathVariable("planName") @Parameter(description = "planName to upgrade to") final String planName){
        long desiredVersion = getVersionFromIfMatchHeader(request.getHeader("If-Match"));
        UUID userid = decoder.getIdbyToken(request);

        final var sub= service.changePlanFromSub(userid,desiredVersion,planName);
        return ResponseEntity.ok().eTag(Long.toString(sub.getVersion())).body(subscriptionViewMapper.toSubscriptionView(sub));
    }

    @Operation(summary = "Renew the subscription of the logged in user")
    @PatchMapping(value = "/renew")
    @RolesAllowed(Role.SUBSCRIBER)
    public ResponseEntity<SubscriptionView> renew(HttpServletRequest request){
        long desiredVersion = getVersionFromIfMatchHeader(request.getHeader("If-Match"));
        UUID userid = decoder.getIdbyToken(request);

        final var sub = service.renew(userid,desiredVersion);
        return ResponseEntity.ok().eTag(Long.toString(sub.getVersion())).body(subscriptionViewMapper.toSubscriptionView(sub));
    }

    @Operation(summary = "Cancel the Subscription of the logged in subscriber")
    @PatchMapping(value = "/cancel")
    @RolesAllowed(Role.SUBSCRIBER)
    public ResponseEntity<SubscriptionView> cancelSubscription(HttpServletRequest request){
        long desiredVersion = getVersionFromIfMatchHeader(request.getHeader("If-Match"));
        UUID userid = decoder.getIdbyToken(request);

        final var subscription = service.cancel(userid,desiredVersion);
        return ResponseEntity.ok().eTag(Long.toString(subscription.getVersion())).body(subscriptionViewMapper.toSubscriptionView(subscription));
    }
}
