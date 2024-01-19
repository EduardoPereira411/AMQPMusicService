package com.plan.PlanQueryModule.planmanagement.api;

import com.plan.PlanQueryModule.planmanagement.model.Role;
import com.plan.PlanQueryModule.planmanagement.services.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Plans", description = "Endpoints for managing Plans")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/plan")
public class PlanController {

    private final PlanService service;


    private final PlanViewMapper planViewMapper;

    @Operation(summary = "Gets all Plans")
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @RolesAllowed({Role.MARKETING_DIRECTOR_ADMIN,Role.USER_ADMIN,Role.PRODUCT_MANAGER_ADMIN})
    public Map<String, Object> findAll(){
        return planViewMapper.toAllPlansMap(service.findAll());
    }

    @Operation(summary = "Gets all Active Plans")
    @GetMapping("/all/active")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> findAllActive(){
        return planViewMapper.toAllPlansMap(service.findAllActive());
    }

    @Operation(summary = "Gets a specific Plan")
    @GetMapping(value = "/id/{planId}")
    public ResponseEntity<PlanView> findSpecificPlanById(@PathVariable("planId")
                                                         @Parameter(description = "The name of the Plan to find") final UUID planId){
        final var plan = service.findByPlanId(planId);
        return ResponseEntity.ok().eTag(Long.toString(plan.getVersion())).body(planViewMapper.toPlanView(plan));
    }

    @Operation(summary = "Gets a specific Plan By its name")
    @GetMapping(value = "/{planName}")
    public ResponseEntity<PlanView> findSpecificPlanByName(@PathVariable("planName")
                                                           @Parameter(description = "The name of the Plan to find") final String planName){
        final var plan = service.findByPlanName(planName);

        return ResponseEntity.ok().eTag(Long.toString(plan.getVersion())).body(planViewMapper.toPlanView(plan));

    }

    @Operation(summary = "Gets a specific Active Plan By its name")
    @GetMapping(value = "/active/{planName}")
    public ResponseEntity<PlanView> findSpecificActivePlanByName(@PathVariable("planName")
                                                                 @Parameter(description = "The name of the Plan to find") final String planName){
        final var plan = service.findByPlanNameAndActive(planName);
        return ResponseEntity.ok().eTag(Long.toString(plan.getVersion())).body(planViewMapper.toPlanView(plan));

    }
}
