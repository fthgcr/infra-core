package com.infracore.security;

import com.infracore.entity.Role;
import com.infracore.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class UserPrincipal implements UserDetails {
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;
    private final Set<Role> roles;

    public static UserPrincipal create(User user) {
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toList());

        return UserPrincipal.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(user.isEnabled())
                .roles(user.getRoles())
                .build();
    }

    // Helper methods for role checking
    public boolean hasRole(Role.RoleName roleName) {
        return roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    public Set<String> getRoleNames() {
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }

    public String getPrimaryRole() {
        // Return the first role or a default role
        return roles.stream()
                .findFirst()
                .map(role -> role.getName().name())
                .orElse("USER");
    }
}
