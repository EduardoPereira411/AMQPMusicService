package com.plan.PlanCommandModule.planmanagement.services;


import com.plan.PlanCommandModule.planmanagement.api.CreatePlanRequest;
import com.plan.PlanCommandModule.planmanagement.api.EditPlanRequest;
import com.plan.PlanCommandModule.planmanagement.model.Plan;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

public interface PlanService {

    Plan findByPlanId(UUID id);

    Plan findByPlanName(String planName);

    Plan create( CreatePlanRequest resource);

    Plan createBonusPlan( CreatePlanRequest resource, UUID subId);

    Plan updateDetails(String planName, EditPlanRequest resource, long desiredVersion);

    void updatePlanAMQP(Plan updatedPlan);

    Plan deactivate(String planName, long desiredVersion);

}
