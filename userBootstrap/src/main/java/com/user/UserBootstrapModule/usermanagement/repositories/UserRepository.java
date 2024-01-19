package com.user.UserBootstrapModule.usermanagement.repositories;


import com.user.UserBootstrapModule.exceptions.NotFoundException;
import com.user.UserBootstrapModule.usermanagement.model.User;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@CacheConfig(cacheNames = "users")
public interface UserRepository extends CrudRepository<User, UUID> {

    @Override
    @CacheEvict(allEntries = true)
    <S extends User> List<S> saveAll(Iterable<S> entities);

    @Override
    @Caching(evict = { @CacheEvict(key = "#p0.id", condition = "#p0.id != null"),
            @CacheEvict(key = "#p0.username", condition = "#p0.username != null") })
    <S extends User> S save(S entity);

    @Cacheable
    Optional<User> findById(UUID id);

    @Cacheable
    Optional<User> findByUsername(String username);

    @Cacheable
    default User getById(final UUID id) {
        final Optional<User> maybeUser = findById(id);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybeUser.filter(User::isEnabled).orElseThrow(() -> new NotFoundException(User.class, String.valueOf(id)));
    }

    @Cacheable
    default User getByUsername(final String username) {
        final Optional<User> maybeUser = findByUsername(username);
        // throws 404 Not Found if the user does not exist or is not enabled
        return maybeUser.filter(User::isEnabled).orElseThrow(() -> new NotFoundException(User.class, username));
    }
}


