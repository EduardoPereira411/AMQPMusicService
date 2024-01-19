package com.plan.PlanCommandModule.planmanagement.services;

import com.plan.PlanCommandModule.planmanagement.api.CreatePlanRequest;
import com.plan.PlanCommandModule.planmanagement.api.EditPlanRequest;
import com.plan.PlanCommandModule.planmanagement.model.Plan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public abstract class EditPlanMapper {

    public abstract Plan create(CreatePlanRequest request);

    public abstract void update(EditPlanRequest request, @MappingTarget Plan plan);
}

