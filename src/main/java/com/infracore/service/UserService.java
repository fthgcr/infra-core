package com.infracore.service;

import com.infracore.dto.UserDTO;
import com.infracore.entity.Role;
import com.infracore.entity.User;
import com.infracore.repository.UserRepository;
import com.infracore.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // Check if user account is active
        if (!user.isActive()) {
            throw new UsernameNotFoundException("ACCOUNT_INACTIVE:" + username);
        }
        
        return UserPrincipal.create(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Role bazlı kullanıcı getirme metodları
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(Role.RoleName roleName) {
        System.out.println("UserService.getUsersByRole() called with role: " + roleName);
        List<User> users = userRepository.findByRoleName(roleName);
        System.out.println("Found " + users.size() + " users with role " + roleName + " from database");
        
        for (User user : users) {
            System.out.println("  DB User: " + user.getFirstName() + " " + user.getLastName() + 
                " (ID: " + user.getId() + ", enabled: " + user.isEnabled() + 
                ", active: " + user.isActive() + ", roles: " + user.getRoleNames() + ")");
        }
        
        List<UserDTO> result = users.stream()
                .map(this::convertToDTO)
                .toList();
                
        System.out.println("Converted to " + result.size() + " DTOs");
        return result;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getActiveUsersByRole(Role.RoleName roleName) {
        return userRepository.findActiveUsersByRoleName(roleName).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRoles(List<Role.RoleName> roleNames) {
        return userRepository.findByRoleNames(roleNames).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getActiveUsersByRoles(List<Role.RoleName> roleNames) {
        return userRepository.findActiveUsersByRoleNames(roleNames).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllAdmins() {
        return userRepository.findAllAdmins().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllClients() {
        List<UserDTO> clients = getUsersByRole(Role.RoleName.USER);
        System.out.println("UserService.getAllClients() - Found " + clients.size() + " users with USER role");
        for (UserDTO client : clients) {
            System.out.println("  - " + client.getFirstName() + " " + client.getLastName() + 
                " (enabled: " + client.isEnabled() + ", active: " + client.isActive() + ")");
        }
        return clients;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getActiveClients() {
        return getActiveUsersByRole(Role.RoleName.USER);
    }

    // Sayım metodları
    @Transactional(readOnly = true)
    public Long countUsersByRole(Role.RoleName roleName) {
        return userRepository.countByRoleName(roleName);
    }

    @Transactional(readOnly = true)
    public Long countActiveUsersByRole(Role.RoleName roleName) {
        return userRepository.countActiveByRoleName(roleName);
    }

    // User DTO dönüştürme metodu
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setNotes(user.getNotes());
        dto.setEnabled(user.isEnabled());
        dto.setActive(user.isActive());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setUpdatedDate(user.getUpdatedDate());
        
        // Rolleri string set olarak ekle
        if (user.getRoles() != null) {
            Set<String> roleNames = user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(java.util.stream.Collectors.toSet());
            dto.setRoles(roleNames);
        }
        
        return dto;
    }

    // User by ID getirme metodu
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    // User entity by ID getirme metodu
    @Transactional(readOnly = true)
    public Optional<User> findByIdEntity(Long id) {
        return userRepository.findById(id);
    }

    // User by username getirme (DTO)
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDTO);
    }

    // User by username getirme (Entity)
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // Username kontrolü
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // Email kontrolü
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // User oluşturma
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // User kaydetme
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // User güncelleme
    @Transactional
    public Optional<UserDTO> updateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(userDTO.getUsername());
                    existingUser.setEmail(userDTO.getEmail());
                    existingUser.setFirstName(userDTO.getFirstName());
                    existingUser.setLastName(userDTO.getLastName());
                    existingUser.setPhoneNumber(userDTO.getPhoneNumber());
                    existingUser.setAddress(userDTO.getAddress());
                    existingUser.setNotes(userDTO.getNotes());
                    existingUser.setEnabled(userDTO.isEnabled());
                    existingUser.setActive(userDTO.isActive());
                    return convertToDTO(userRepository.save(existingUser));
                });
    }

    // User silme
    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
