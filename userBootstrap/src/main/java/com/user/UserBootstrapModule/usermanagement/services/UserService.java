package com.user.UserBootstrapModule.usermanagement.services;


import com.user.UserBootstrapModule.exceptions.ConflictException;
import com.user.UserBootstrapModule.exceptions.NotFoundException;
import com.user.UserBootstrapModule.usermanagement.model.Role;
import com.user.UserBootstrapModule.usermanagement.model.User;
import com.user.UserBootstrapModule.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;




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

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with username - %s, not found", username)));
    }

}
