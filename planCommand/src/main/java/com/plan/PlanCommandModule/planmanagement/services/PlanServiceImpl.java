package com.plan.PlanCommandModule.planmanagement.services;

import com.plan.PlanCommandModule.AMQP.AMQPSender;
import com.plan.PlanCommandModule.exceptions.ConflictException;
import com.plan.PlanCommandModule.exceptions.NotFoundException;
import com.plan.PlanCommandModule.planmanagement.api.CreatePlanRequest;
import com.plan.PlanCommandModule.planmanagement.api.EditPlanRequest;
import com.plan.PlanCommandModule.planmanagement.model.Plan;
import com.plan.PlanCommandModule.planmanagement.repositories.PlanRepository;
//import com.plan.PlanModule.planmanagement.api.ChangePriceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService{

    private final PlanRepository planRepo;
    private final AMQPSender sender;


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
    public Plan create(final CreatePlanRequest resource){
        try {
            findByPlanName(resource.getPlanName());
        } catch (NotFoundException ex) {
            final Plan plan = new Plan(
                    resource.getPlanName(),
                    resource.getMinutes(),
                    resource.getMonthlyFee(),
                    resource.getAnnualFee(),
                    resource.getDeviceNr(),
                    resource.getMusicCollectionsNr(),
                    resource.getMusicRecommendation()
            );

            Plan createdPlan = planRepo.save(plan);
            sender.sendCreatePlan(createdPlan);
            return createdPlan;
        }
        throw new ConflictException("There is already a plan with the specified Name");
    }


    @Transactional
    @Override
    public Plan createBonusPlan(final CreatePlanRequest resource, UUID userId){
        try {
            findByPlanName(resource.getPlanName());
        } catch (NotFoundException ex) {
            final Plan plan = new Plan(
                    resource.getPlanName(),
                    resource.getMinutes(),
                    resource.getMonthlyFee(),
                    resource.getAnnualFee(),
                    resource.getDeviceNr(),
                    resource.getMusicCollectionsNr(),
                    resource.getMusicRecommendation(),
                    true
            );
            Plan createdPlan = planRepo.save(plan);
            final var response = sender.sendAskCreateBonusPlan(userId.toString(),createdPlan.getPlanID().toString());
            if(response){
                sender.sendCreatePlan(createdPlan);
                return createdPlan;
            }else{
                throw new RuntimeException("Error creating Bonus Plan");
            }
        }
        throw new ConflictException("There is already a plan with the specified Name");
    }


    @Override
    public Plan updateDetails(String planName, final EditPlanRequest resource, long desiredVersion){
        final var plan = planRepo.findByPlanName(planName);
        if(!planName.equals(resource.getPlanName())) {
            try {
                findByPlanName(resource.getPlanName());
            } catch (NotFoundException ex) {
                if (plan.isPresent()) {
                    plan.get().changePlanDetails(desiredVersion, resource.getPlanName(), resource.getMinutes(),
                            resource.getDeviceNr(), resource.getMusicCollectionsNr(),
                            resource.getMusicRecommendation(), resource.getMonthlyFee(), resource.getAnnualFee());

                    Plan updatedPlan = planRepo.save(plan.get());
                    sender.sendUpdatedPlan(updatedPlan);
                    return updatedPlan;
                }
            }
            throw new ConflictException("There is already a plan with the specified Name");
        } if (plan.isPresent()) {
            plan.get().changePlanDetails(desiredVersion, resource.getPlanName(), resource.getMinutes(),
                    resource.getDeviceNr(), resource.getMusicCollectionsNr(),
                    resource.getMusicRecommendation(), resource.getMonthlyFee(), resource.getAnnualFee());

            Plan updatedPlan = planRepo.save(plan.get());
            sender.sendUpdatedPlan(updatedPlan);
            return updatedPlan;
        } else {
            throw  new NotFoundException("Selected Plan doesn't exist");
        }
    }

    @Override
    public Plan deactivate(final String planName, long desiredVersion){
        final var localresult = planRepo.findPlanIfActiveByName(planName);
        if(localresult.isPresent()) {
            localresult.get().deactivatePlan(desiredVersion);

            Plan updatedPlan = planRepo.save(localresult.get());
            sender.sendUpdatedPlan(updatedPlan);
            return updatedPlan;
        }else {
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
