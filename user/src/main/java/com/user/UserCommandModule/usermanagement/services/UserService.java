package com.user.UserCommandModule.usermanagement.services;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import com.user.UserCommandModule.auth.api.AuthRequest;
import com.user.UserCommandModule.exceptions.NotFoundException;
import com.user.UserCommandModule.usermanagement.api.*;
import com.user.UserCommandModule.usermanagement.model.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user.UserCommandModule.exceptions.ConflictException;
import com.user.UserCommandModule.usermanagement.model.User;
import com.user.UserCommandModule.usermanagement.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;
    private final EditUserMapper userEditMapper;
    private final UserViewMapper userViewMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createAdmin(final CreateUserRequest request){
        if (findUserByName(request.getUsername()).isPresent()) {
            throw new ConflictException("Username already exists!");
        }
        if (!request.getPassword().equals(request.getRePassword())) {
            throw new ValidationException("Passwords don't match!");
        }

        Set<String> requestedAuthorities = request.getAuthorities();
        boolean isValidAuthority = requestedAuthorities.stream()
                .anyMatch(authority -> Role.isValidAdminRole(authority));

        if (!isValidAuthority) {
            throw new ValidationException("Admin Role not found, Role must be one presented in documents!");
        }
        User user = userEditMapper.createAdmin(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepo.save(user);
    }

    @Transactional
    public User createCustomer(final CreateCustomerRequest request){
        try {
            findUserByName(request.getUsername());
            throw new ConflictException("Username already exists!");
        }catch (NotFoundException ex) {
            if (!request.getPassword().equals(request.getRePassword())) {
                throw new ValidationException("Passwords don't match!");
            }

            User user = userEditMapper.createCustomer(request);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            Role newRoleObj = new Role("NEW_USER");
            user.addAuthority(newRoleObj);

            return userRepo.save(user);
        }
    }


    public User flipUserSubscription(UUID userId){
        final var localuser = userRepo.findById(userId);
        if(localuser.isPresent()){
            User finalUser = localuser.get();
            finalUser.flipSubscriptionRole();
            return userRepo.save(finalUser);
        }else{
            throw new NotFoundException("User doesn't exist");
        }
    }
    public Optional<User> getUserById(final UUID id){
        Optional<User> localResult = userRepo.findById(id);

        if (localResult.isPresent()) {
            return localResult;
        } else {
            return Optional.empty();
        }
    }

    public Optional<User> findUserByName(String username) {
        Optional<User> localResult = userRepo.findByUsername(username);
        if (localResult.isPresent()) {
            return localResult;
        } else {
            throw new NotFoundException("User doesn't exist");
        }
    }

    @Transactional
    public UserView delete(final UUID id) {
        User user = userRepo.getById(id);

        // user.setUsername(user.getUsername().replace("@", String.format("_%s@",
        // user.getId().toString())));
        user.setEnabled(false);
        user = userRepo.save(user);

        return userViewMapper.toUserView(user);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with username - %s, not found", username)));
    }

}
