package com.plan.PlanBootstrapModule.planmanagement.repositories;

import com.plan.PlanBootstrapModule.planmanagement.model.Plan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;


public interface PlanRepository extends CrudRepository<Plan, UUID> {

    Optional<Plan> findByPlanID(UUID id);

    Optional<Plan> findByPlanName(String planName);

    @Query("SELECT p FROM Plan p WHERE p.planName LIKE :planNamepat AND p.isActive = true")
    Optional<Plan> findPlanIfActiveByName(@Param("planNamepat") String planNamepat);

    @Query("SELECT p FROM Plan p WHERE p.isActive = true")
    Iterable<Plan> findAllActivePlans();


}

