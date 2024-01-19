package com.subscription.SubscriptionQueryModule.subscriptionmanagement.services;


import com.subscription.SubscriptionQueryModule.exceptions.NotFoundException;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.model.Plan;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.model.Subscription;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.repositories.PlanRepo.PlanRepository;
import com.subscription.SubscriptionQueryModule.subscriptionmanagement.repositories.SubscriptionRepo.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;

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
    public Subscription findActiveByUserToken(UUID userid){
        return findActiveByUser(userid);
    }

    @Override
    public Plan getPlanFromSub(UUID userid){
        Subscription sub = findActiveByUser(userid);
        final var plan = planRepository.getPlanByPlanID(sub.getPlanId());
        if(plan.isPresent()){
            return plan.get();
        }else {
            throw new NotFoundException("Your plan is no longer available");
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
}


