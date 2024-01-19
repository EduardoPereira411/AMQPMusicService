package com.subscription.SubscriptionCommandModule.subscriptionmanagement.services;


import java.util.*;

import com.subscription.SubscriptionCommandModule.AMQP.AMQPSender;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.BonusPlanRequest;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.repositories.PlanRepo.PlanRepository;
import com.subscription.SubscriptionCommandModule.exceptions.ConflictException;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.Plan;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.Subscription;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.repositories.SubscriptionRepo.SubscriptionRepository;
import org.springframework.stereotype.Service;

import com.subscription.SubscriptionCommandModule.exceptions.NotFoundException;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.api.CreateSubscriptionRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final AMQPSender sender;

    @Override
    public Iterable<Subscription> findAll() {
        return subscriptionRepository.findAll();
    }

    @Override
    public Subscription findByUUID(final UUID id){
        return subscriptionRepository.findBySubID(id).orElseThrow(() -> new NotFoundException("The Subscription doesn't exist"));
    }

    @Override
    public Subscription findActiveByUser(UUID userid) {

        return subscriptionRepository.findNonBonusActiveSubscriptionByUserId(userid).orElseThrow(() -> new NotFoundException("The Subscription doesn't exist"));
    }


    @Override
    public Subscription create(final CreateSubscriptionRequest resource, UUID userId){
        Optional<Plan> plan= planRepository.findPlanIfActiveByName(resource.getPlanName());
        if(plan.isPresent()) {
            UUID planId = plan.get().getPlanID();
            try{
                findActiveByUser(userId);
            }catch (NotFoundException ex){
                resource.setUserId(userId);
                final Subscription sub = new Subscription(resource.getPaymentMode(), planId, resource.getUserId());
                Subscription finalSub = subscriptionRepository.save(sub);
                sender.sendCreatedSub(finalSub);
                sender.sendFlipUser(userId.toString());
                return sub;
            }
        }else{
            throw new NotFoundException("The Plan you mentioned is either unavailable or doesn't exist");
        }
        throw new ConflictException("User is already Subscribed");
    }


    @Override
    public Subscription cancel(UUID userId, long desiredVersion){
        Optional<Subscription> localResult= subscriptionRepository.findNonBonusActiveSubscriptionByUserId(userId);

            if (localResult.isPresent()) {
                Subscription finalSub = localResult.get();
                finalSub.cancelSubscription(desiredVersion);
                Subscription sendSub = subscriptionRepository.save(finalSub);
                sender.sendUpdatedSub(sendSub);
                sender.sendFlipUser(sendSub.getUserId().toString());
                return finalSub;
            } else {
                throw new NotFoundException("The User is not subscribed to a plan");
            }
    }


    @Override
    public Subscription changePlanFromSub(UUID userId, long desiredVersion, String planName){
        final var newplan= planRepository.findPlanIfActiveByName(planName).orElseThrow(() -> new NotFoundException("The plan doesnt exist"));
        UUID planId= newplan.getPlanID();
        final var sub = subscriptionRepository.findNonBonusActiveSubscriptionByUserId(userId);
        if(sub.isPresent()){
            Subscription finalSub = sub.get();
            finalSub.changePlan(planId, desiredVersion);
            Subscription sendSub = subscriptionRepository.save(finalSub);
            sender.sendUpdatedSub(sendSub);
            return subscriptionRepository.save(finalSub);
        }else {
            throw new  NotFoundException("The Subscription doesn't exist");
        }
    }

    @Override
    public Subscription renew(UUID userId, long desiredVersion){
        Optional<Subscription> localResult = subscriptionRepository.findNonBonusActiveSubscriptionByUserId(userId);

        if (localResult.isPresent()) {
            Subscription finalSub = localResult.get();
            finalSub.renewSub(desiredVersion);
            Subscription sendSub = subscriptionRepository.save(finalSub);
            sender.sendUpdatedSub(sendSub);
            return subscriptionRepository.save(finalSub);
        } else {
            throw new NotFoundException("The Subscription doesn't exist");
        }

    }

    @Override
    public void updateSubAMQP(Subscription subscription) {
        final var isSub = subscriptionRepository.findBySubID(subscription.getSubID());
        if (isSub.isPresent()) {
            final var finalSub = isSub.get();
            finalSub.update(subscription);
            subscriptionRepository.save(finalSub);
        }
    }

    @Override
    public boolean createBonusSubAMQP(BonusPlanRequest request){
        final var isValidUser = subscriptionRepository.findNonBonusActiveSubscriptionByUserId(UUID.fromString(request.getUserId()));
        if(isValidUser.isPresent()){
            final var isValidUserBonus = subscriptionRepository.findBonusActiveSubscriptionByUserId(UUID.fromString(request.getUserId()));
            if (isValidUserBonus.isEmpty()) {
                Subscription newBonusSub = new Subscription(UUID.fromString(request.getPlanId()), UUID.fromString(request.getUserId()), true);
                System.out.println("Bonus Sub " + newBonusSub.getSubID() + " created");
                subscriptionRepository.save(newBonusSub);
                sender.sendCreatedSub(newBonusSub);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}


