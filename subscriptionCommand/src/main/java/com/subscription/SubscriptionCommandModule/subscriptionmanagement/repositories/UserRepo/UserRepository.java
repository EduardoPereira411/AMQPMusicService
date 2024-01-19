package com.subscription.SubscriptionCommandModule.subscriptionmanagement.repositories.UserRepo;

import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.Subscription;
import com.subscription.SubscriptionCommandModule.subscriptionmanagement.model.User;
import org.springframework.data.repository.CrudRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByUserId(UUID userId);

}
