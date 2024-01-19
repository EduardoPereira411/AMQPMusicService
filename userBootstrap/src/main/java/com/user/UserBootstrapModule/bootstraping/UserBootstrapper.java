package com.user.UserBootstrapModule.bootstraping;

import com.user.UserBootstrapModule.AMQP.AMQPSender;
import com.user.UserBootstrapModule.usermanagement.model.Role;
import com.user.UserBootstrapModule.usermanagement.model.User;
import com.user.UserBootstrapModule.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserBootstrapper implements CommandLineRunner {

    private final UserRepository userRepo;

    private final PasswordEncoder encoder;

    private final AMQPSender sender;

    private final RabbitListenerEndpointRegistry registry;

    @Override
    @Transactional
    public void run(final String... args) throws Exception {
        sender.sendGetAllUsers();
        if(userRepo.count()==0) {
            // admin
            if (userRepo.findByUsername("u1@mail.com").isEmpty()) {
                final User u1 = new User(UUID.fromString("a12b3c45-6d78-9e01-f234-56789abcde12"), "u1@mail.com", encoder.encode("Password1"), "User Person", Role.USER_ADMIN);
                userRepo.save(u1);
            }
            if (userRepo.findByUsername("marketing@mail.com").isEmpty()) {
                final User u2 = new User(UUID.fromString("12345678-90ab-cdef-1234-56789abcdef0"), "marketing@mail.com", encoder.encode("Password1"), "Marketing Person", Role.MARKETING_DIRECTOR_ADMIN);
                userRepo.save(u2);
            }
            if (userRepo.findByUsername("product@mail.com").isEmpty()) {
                final User u3 = new User(UUID.fromString("abcdef01-2345-6789-abcd-ef0123456789"), "product@mail.com", encoder.encode("Password1"), "Product Person", Role.PRODUCT_MANAGER_ADMIN);
                userRepo.save(u3);
            }
            if (userRepo.findByUsername("daniel@mail.com").isEmpty()) {
                final User u4 = new User(UUID.fromString("98765432-10fe-dcba-9876-543210fedcba"), "daniel@mail.com", encoder.encode("Password1"));
                userRepo.save(u4);
            }
        }
        registry.start();
    }
}