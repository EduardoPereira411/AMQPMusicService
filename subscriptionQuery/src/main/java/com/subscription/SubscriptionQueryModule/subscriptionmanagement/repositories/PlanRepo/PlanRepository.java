package com.subscription.SubscriptionQueryModule.subscriptionmanagement.repositories.PlanRepo;

import com.subscription.SubscriptionQueryModule.subscriptionmanagement.model.Plan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends CrudRepository<Plan, UUID> {


    @Query("SELECT p FROM Plan p WHERE p.planName = :planName AND p.isActive = true")
    Optional<Plan> findActivePlanByName(@Param("planName") String planName);

    Optional<Plan> findByPlanName(String planName);

    @Query("SELECT p FROM Plan p WHERE p.planID = :planId")
    Optional<Plan> getPlanByPlanID(UUID planId);
}
