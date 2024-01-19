package com.device.DeviceCommandModule.devicemanagement.repositories.PlanRepo;

import com.device.DeviceCommandModule.devicemanagement.model.Plan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends CrudRepository<Plan,UUID> {

    @Query("SELECT p.deviceNr FROM Plan p WHERE p.planID = :planID AND p.isActive = true")
    int getDeviceNrAllowedByPlan(@Param("planID") UUID planId);

    Optional<Plan> findByPlanName(String planName);

    Optional<Plan> findByPlanID(UUID planID);
}
