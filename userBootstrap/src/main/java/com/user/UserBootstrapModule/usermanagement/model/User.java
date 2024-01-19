package com.user.UserBootstrapModule.usermanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements UserDetails {

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

    @ElementCollection(fetch = FetchType.EAGER)
    @Getter
    @Setter
    private Set<Role> authorities = new HashSet<>();

    public User() {
        this.id = UUID.randomUUID();
    }
    public User(final String username, final String password) {
        this.id = UUID.randomUUID();
        this.username = username;
        setPassword(password);
        addAuthority(new Role(Role.NEW_USER));
    }

    public User(final UUID userId, final String username, final String password) {
        this.id = userId;
        this.username = username;
        setPassword(password);
        addAuthority(new Role(Role.NEW_USER));
    }

    public User(final UUID userId, final String username, final String password, final String fullName, final String role){
        this.id = userId;
        this.username = username;
        setPassword(password);
        setFullName(fullName);
        addAuthority(new Role(role));
    }

    public User(UUID id, Long version, LocalDate createdAt, LocalDate modifiedAt, String createdBy, String modifiedBy, boolean enabled, String username, String password, String fullName, Set<Role> authorities) {
        this.id = id;
        this.version = version;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;
        this.enabled = enabled;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.authorities = authorities;
    }

    public static User newUserWithRole(final String username, final String password, final String fullName, final String role) {
        final var u = new User(username, password);
        u.setFullName(fullName);
        u.addAuthority(new Role(role));
        return u;
    }
    public void flipSubscriptionRole() {
        Role subscriberRole = new Role(Role.SUBSCRIBER);
        Role newUserRole = new Role(Role.NEW_USER);

        if (authorities.contains(subscriberRole)) {
            authorities.remove(subscriberRole);
            authorities.add(newUserRole);
        } else if (authorities.contains(newUserRole)) {
            authorities.remove(newUserRole);
            authorities.add(subscriberRole);
        }
    }

    public void setPassword(final String password) {
        this.password = Objects.requireNonNull(password);
    }

    public void addAuthority(final Role r) {
        authorities.add(r);
    }

    public void removeAuthorities() {
        authorities.clear();
    }

    public void removeAuthority(Role role) {
        authorities.remove(role);
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
