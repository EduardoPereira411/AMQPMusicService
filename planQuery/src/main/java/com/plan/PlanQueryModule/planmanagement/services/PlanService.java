package com.plan.PlanQueryModule.planmanagement.services;


import com.plan.PlanQueryModule.planmanagement.model.Plan;

import java.util.UUID;

public interface PlanService {

    Iterable<Plan> findAll();

    Iterable<Plan> findAllActive();

    Plan findByPlanId(UUID id);

    Plan findByPlanName(String planName);

    Plan findByPlanNameAndActive(String planName);

    void updatePlanAMQP(Plan updatedPlan);

}
