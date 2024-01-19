package com.subscription.SubscriptionBootstrapModule.subscriptionmanagement.services;


import com.subscription.SubscriptionBootstrapModule.exceptions.NotFoundException;
import com.subscription.SubscriptionBootstrapModule.subscriptionmanagement.model.Subscription;
import com.subscription.SubscriptionBootstrapModule.subscriptionmanagement.repositories.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

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
    public void updateSubAMQP(Subscription subscription) {
        final var isSub = subscriptionRepository.findBySubID(subscription.getSubID());
        if (isSub.isPresent()) {
            final var finalSub = isSub.get();
            finalSub.update(subscription);
            subscriptionRepository.save(finalSub);
        }
    }
}


