package com.user.UserCommandModule.usermanagement.services;

import static java.util.stream.Collectors.toSet;

import java.util.HashSet;
import java.util.Set;

import com.user.UserCommandModule.usermanagement.api.CreateCustomerRequest;
import com.user.UserCommandModule.usermanagement.model.Role;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.user.UserCommandModule.usermanagement.api.CreateUserRequest;
import com.user.UserCommandModule.usermanagement.model.User;

@Mapper(componentModel = "spring")
public abstract class EditUserMapper {

    @Mapping(source = "authorities", target = "authorities", qualifiedByName = "stringToRole")
    public abstract User createAdmin(CreateUserRequest request);


    public abstract User createCustomer(CreateCustomerRequest request);

    @Named("stringToRole")
    protected Set<Role> stringToRole(final Set<String> authorities) {
        if (authorities != null) {
            return authorities.stream().map(Role::new).collect(toSet());
        }
        return new HashSet<>();
    }

}
