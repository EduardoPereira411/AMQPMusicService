package com.subscription.SubscriptionQueryModule.subscriptionmanagement.api;

import com.subscription.SubscriptionQueryModule.configuration.KeyDecoder;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.model.Plan;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.model.Role;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.services.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;


@Tag(name = "Subscription", description = "Endpoints for managing Subscriptions")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionController {
    private final SubscriptionService service;
    private final SubscriptionViewMapper subscriptionViewMapper;
    private final KeyDecoder decoder;

    @Operation(summary = "Gets all subscriptions")
    @GetMapping("/all")
    @RolesAllowed({Role.PRODUCT_MANAGER_ADMIN,Role.USER_ADMIN,Role.MARKETING_DIRECTOR_ADMIN})
    public Map<String, Object> findAll(){
        return subscriptionViewMapper.toAllSubscriptionsMap(service.findAll());
    }

    @Operation(summary = "Gets a specific subscription by its id")
    @GetMapping(value = "/{id}")
    @RolesAllowed({Role.PRODUCT_MANAGER_ADMIN,Role.USER_ADMIN,Role.MARKETING_DIRECTOR_ADMIN})
    public ResponseEntity<SubscriptionView> findById(@PathVariable("id") @Parameter(description = "The id of the subscription to find") final UUID id) throws IOException, URISyntaxException, InterruptedException {

        final var subscription = service.findByUUID(id);

        return ResponseEntity.ok().eTag(Long.toString(subscription.getVersion())).body(subscriptionViewMapper.toSubscriptionView(subscription));
    }

    @Operation(summary = "Gets subscription from logged in user")
    @GetMapping
    @RolesAllowed(Role.SUBSCRIBER)
    public ResponseEntity<SubscriptionView> findLoggedInSub(HttpServletRequest request){
        UUID userid = decoder.getIdbyToken(request);

        final var subscription =  service.findActiveByUserToken(userid);

        return ResponseEntity.ok().eTag(Long.toString(subscription.getVersion())).body(subscriptionViewMapper.toSubscriptionView(subscription));
    }


    @Operation(summary = "Gets the Plan to which the logged in user is subscribed to")
    @GetMapping("/plan")
    @RolesAllowed(Role.SUBSCRIBER)
    public ResponseEntity<PlanView> getPlanSubbedTo(HttpServletRequest request){
        UUID userid = decoder.getIdbyToken(request);

        final var plan = service.getPlanFromSub(userid);

        return ResponseEntity.ok().body(subscriptionViewMapper.toPlanView(plan));
    }
}
