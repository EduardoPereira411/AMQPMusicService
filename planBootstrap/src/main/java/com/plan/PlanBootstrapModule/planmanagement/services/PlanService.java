package com.plan.PlanBootstrapModule.planmanagement.services;


import com.plan.PlanBootstrapModule.planmanagement.model.Plan;

import java.util.UUID;

public interface PlanService {

    Iterable<Plan> findAll();

    Iterable<Plan> findAllActive();

    Iterable<Plan> saveAll(Iterable<Plan> plans);

    Plan findByPlanId(UUID id);

    Plan findByPlanName(String planName);

    Plan findByPlanNameAndActive(String planName);

    void updatePlanAMQP(Plan updatedPlan);

}
