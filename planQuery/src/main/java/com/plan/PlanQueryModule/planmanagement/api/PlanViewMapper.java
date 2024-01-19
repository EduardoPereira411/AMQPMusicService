package com.plan.PlanQueryModule.planmanagement.api;

import com.plan.PlanQueryModule.planmanagement.model.Plan;
import org.mapstruct.Mapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class PlanViewMapper {

    public abstract PlanView toPlanView(Plan plan);

    public abstract Iterable<PlanView> toPlanView(Iterable<Plan> plans);

    public Map<String, Object> toAllPlansMap(Iterable<Plan> plans) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("plans", toPlanView(plans));
        return response;
    }

    public Integer mapOptInt(final Optional<Integer> i) {
        return i.orElse(null);
    }

    public Long mapOptLong(final Optional<Long> i) {
        return i.orElse(null);
    }

    public String mapOptString(final Optional<String> i) {
        return i.orElse(null);
    }

    public UUID mapOptUUID(final Optional<UUID> i) {
        return i.orElse(null);
    }

}
