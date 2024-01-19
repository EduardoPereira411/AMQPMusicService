package com.plan.PlanQueryModule.planmanagement.services;

import com.plan.PlanQueryModule.planmanagement.repositories.PlanRepository;
import com.plan.PlanQueryModule.exceptions.NotFoundException;
//import com.plan.PlanModule.planmanagement.api.ChangePriceRequest;
import com.plan.PlanQueryModule.planmanagement.model.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService{

    private final PlanRepository planRepo;

    @Override
    public Iterable<Plan> findAll(){
        return planRepo.findAll();
    }
    @Override
    public Iterable<Plan> findAllActive(){
        return planRepo.findAllActivePlans();
    }

    @Override
    public Plan findByPlanId(final UUID id){
        final var localPlan = planRepo.findByPlanID(id);
        if(localPlan.isPresent()){
            return localPlan.get();
        }else {
            throw  new NotFoundException("Selected Plan doesn't exist");
        }
    }

    @Override
    public Plan findByPlanName(String planName){
        Optional<Plan> localResult = planRepo.findByPlanName(planName);
        if (localResult.isPresent()) {
            return localResult.get();
        } else {
            throw  new NotFoundException("Selected Plan doesn't exist");
        }
    }
    @Override
    public Plan findByPlanNameAndActive(String planName){
        Optional<Plan> localResult = planRepo.findPlanIfActiveByName(planName);
        if (localResult.isPresent()) {
            return localResult.get();
        } else {
            throw  new NotFoundException("Selected Plan doesn't exist");
        }
    }

    @Override
    public void updatePlanAMQP(Plan updatedPlan){
        try {
            final var plan = findByPlanId(updatedPlan.getPlanID());
            plan.updatePlan(updatedPlan);
            planRepo.save(plan);
        } catch (NotFoundException ex) {
            throw new NotFoundException("Plan was not found");
        }
    }

}
