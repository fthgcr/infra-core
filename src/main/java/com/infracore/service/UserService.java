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

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
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
        return userRepository.findByRoleName(roleName).stream()
                .map(this::convertToDTO)
                .toList();
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

    // Özel role metodları
    @Transactional(readOnly = true)
    public List<UserDTO> getAllLawyers() {
        return userRepository.findAllLawyers().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getActiveLawyers() {
        return userRepository.findActiveLawyers().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllClients() {
        return userRepository.findAllClients().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getActiveClients() {
        return userRepository.findActiveClients().stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllAdmins() {
        return userRepository.findAllAdmins().stream()
                .map(this::convertToDTO)
                .toList();
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

    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByIdEntity(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public Optional<UserDTO> updateUser(Long id, UserDTO userDTO) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(userDTO.getUsername());
                    existingUser.setEmail(userDTO.getEmail());
                    existingUser.setFirstName(userDTO.getFirstName());
                    existingUser.setLastName(userDTO.getLastName());
                    return convertToDTO(userRepository.save(existingUser));
                });
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.isEnabled());
        dto.setActive(user.isActive());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setNotes(user.getNotes());
        dto.setCreatedDate(user.getCreatedDate());
        dto.setUpdatedDate(user.getUpdatedDate());
        dto.setRoles(user.getRoleNames());
        return dto;
    }
}
