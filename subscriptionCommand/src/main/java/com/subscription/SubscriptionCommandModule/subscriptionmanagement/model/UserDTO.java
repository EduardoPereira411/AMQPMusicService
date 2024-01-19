package com.subscription.SubscriptionCommandModule.subscriptionmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.apache.tomcat.jni.SSL.setPassword;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", version=" + version +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                ", createdBy='" + createdBy + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", enabled=" + enabled +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", fullName='" + fullName + '\'' +
                ", authorities=" + authorities +
                '}';
    }

    // database primary key
    @Id
    @Getter
    @Setter
    private UUID id;

    // optimistic lock concurrency control
    @Version
    @Getter
    @Setter
    private Long version;

    // auditing info
    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Getter
    @Setter
    private LocalDate createdAt;

    // auditing info
    @LastModifiedDate
    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDate modifiedAt;

    // auditing info
    @CreatedBy
    @Column(nullable = false, updatable = false)
    @Getter
    @Setter
    private String createdBy;

    // auditing info
    @LastModifiedBy
    @Column(nullable = false)
    private String modifiedBy;

    @Setter
    @Getter
    private boolean enabled = true;

    @Column(unique = true, updatable = false, nullable = false)
    @Email
    @Getter
    @Setter
    @NotNull
    @NotBlank
    private String username;

    @Column(nullable = false)
    @Getter
    @NotNull
    @NotBlank
    private String password;

    @Getter
    @Setter
    private String fullName;

    @ElementCollection
    @Getter
    @Setter
    private Set<Role> authorities = new HashSet<>();

    public UserDTO() {
        this.id = UUID.randomUUID();
    }

    public UserDTO(final String username, final String password) {
        this.id = UUID.randomUUID();
        this.username = username;
        setPassword(password);
        addAuthority(new Role(Role.NEW_USER));
    }

    public UserDTO(final UUID userId, final String username, final String password) {
        this.id = userId;
        this.username = username;
        setPassword(password);
        addAuthority(new Role(Role.NEW_USER));
    }

    public UserDTO(final UUID userId, final String username, final String password, final String fullName, final String role) {
        this.id = userId;
        this.username = username;
        setPassword(password);
        setFullName(fullName);
        addAuthority(new Role(role));
    }

    public void addAuthority(final Role r) {
        authorities.add(r);
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }
}