package com.user.UserCommandModule.usermanagement.api;

import com.user.UserCommandModule.usermanagement.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserView {
    private UUID id;

    private String username;
    private String fullName;
    private Set<Role> authorities;
}
