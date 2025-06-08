package com.infracore.dto;

import com.infracore.entity.User;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private boolean isActive;
    private Set<String> roles;

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.isEnabled());
        dto.setActive(user.isActive());
        dto.setRoles(user.getRoleNames());
        return dto;
    }
} 