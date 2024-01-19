package com.plan.PlanBootstrapModule.planmanagement.services;

import com.plan.PlanBootstrapModule.exceptions.NotFoundException;
import com.plan.PlanBootstrapModule.planmanagement.model.Plan;
import com.plan.PlanBootstrapModule.planmanagement.repositories.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public Iterable<Plan> saveAll(Iterable<Plan> plans) {
        List<Plan> savedPlans = new ArrayList<>();

        for (Plan plan : plans) {
            if (!planRepo.existsById(plan.getPlanID())) {
                savedPlans.add(planRepo.save(plan));
            } else {
                System.out.println("Plan with ID " + plan.getPlanID() + " already exists. Skipping save.");
            }
        }
        return savedPlans;
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
