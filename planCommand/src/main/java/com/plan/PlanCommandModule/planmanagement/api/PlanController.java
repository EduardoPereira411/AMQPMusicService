package com.plan.PlanCommandModule.planmanagement.api;

import com.plan.PlanCommandModule.AMQP.AMQPSender;
import com.plan.PlanCommandModule.planmanagement.model.Role;
import com.plan.PlanCommandModule.planmanagement.services.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Plans", description = "Endpoints for managing Plans")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plan")
public class PlanController {

    private final PlanService service;

    private final PlanViewMapper planViewMapper;

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

    @Operation(summary = "Creates a new Plan")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed(Role.MARKETING_DIRECTOR_ADMIN)
    public ResponseEntity<PlanView> create(@Valid @RequestBody final CreatePlanRequest resource){


        final var plan = service.create(resource);
        final var newplanUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(plan.getPlanID().toString())
                .build().toUri();

        return ResponseEntity.created(newplanUri).eTag(Long.toString(plan.getVersion()))
                .body(planViewMapper.toPlanView(plan));
    }

    @Operation(summary = "Creates a new Bonus plan for a subscriber")
    @PostMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RolesAllowed(Role.MARKETING_DIRECTOR_ADMIN)
    public ResponseEntity<PlanView> createBonusPlan(@PathVariable("userId") @Parameter(description = "The Id of the sub to be associated with the plan") final UUID userId,
                                                    @Valid @RequestBody final CreatePlanRequest resource){


        final var plan = service.createBonusPlan(resource, userId);
        final var newplanUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(plan.getPlanID().toString())
                .build().toUri();

        return ResponseEntity.created(newplanUri).eTag(Long.toString(plan.getVersion()))
                .body(planViewMapper.toPlanView(plan));
    }

    @Operation(summary = "Partially updates an existing plan")
    @PatchMapping(value = "/{planName}")
    @RolesAllowed(Role.MARKETING_DIRECTOR_ADMIN)
    public ResponseEntity<PlanView> updatePlan(final WebRequest request,
                                               @PathVariable("planName") @Parameter(description = "The name of the plan to update") final String planName,
                                               @Valid @RequestBody final EditPlanRequest resource){
        long desiredVersion = getVersionFromIfMatchHeader(request.getHeader("If-Match"));

        final var plan = service.updateDetails(planName, resource, desiredVersion);
        return ResponseEntity.ok().eTag(Long.toString(plan.getVersion())).body(planViewMapper.toPlanView(plan));
    }

    @Operation(summary = "Deactivates an existing plan")
    @PatchMapping(value = "/deactivate/{planName}")
    @RolesAllowed(Role.MARKETING_DIRECTOR_ADMIN)
    public ResponseEntity<PlanView> deactivatePlan(final WebRequest request,
                                                   @PathVariable("planName") @Parameter(description = "The name of the plan to deactivate") final String planName){
        long desiredVersion = getVersionFromIfMatchHeader(request.getHeader("If-Match"));

        final var plan = service.deactivate(planName, desiredVersion);
        return ResponseEntity.ok().eTag(Long.toString(plan.getVersion())).body(planViewMapper.toPlanView(plan));
    }
}
