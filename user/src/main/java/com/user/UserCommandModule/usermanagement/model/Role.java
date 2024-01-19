package com.user.UserCommandModule.usermanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
@Data
@AllArgsConstructor
public class Role implements GrantedAuthority {

    private static final long serialVersionUID = 1L;

    public static final String USER_ADMIN = "USER_ADMIN";

    public static final String MARKETING_DIRECTOR_ADMIN = "MARKETING_DIRECTOR_ADMIN";
    public static final String PRODUCT_MANAGER_ADMIN = "PRODUCT_MANAGER_ADMIN";
    public static final String NEW_USER = "NEW_USER";
    public static final String SUBSCRIBER = "SUBSCRIBER";


    private String authority;

    public Role() {
    }

    public static boolean isValidAdminRole(String role) {
        // Check if the role matches any of the predefined roles
        return role.equals(USER_ADMIN) || role.equals(MARKETING_DIRECTOR_ADMIN)
                || role.equals(PRODUCT_MANAGER_ADMIN);
    }

    public static boolean isValidRole(String role) {
        // Check if the role matches any of the predefined roles
        return role.equals(NEW_USER) || role.equals(SUBSCRIBER);
    }

}
