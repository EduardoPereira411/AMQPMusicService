package com.user.UserCommandModule.usermanagement.api;


import com.user.UserCommandModule.usermanagement.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserViewMapper {
    public abstract UserView toUserView(User user);

    public abstract List<UserView> toUserView(List<User> users);
}
