package com.infracore.service;

import com.infracore.dto.LoginRequest;
import com.infracore.dto.LoginResponse;
import com.infracore.dto.RegistrationRequest;
import com.infracore.dto.RegistrationResponse;
import com.infracore.dto.UserDTO;
import com.infracore.entity.Role;
import com.infracore.entity.User;
import com.infracore.exception.UserAlreadyExistsException;
import com.infracore.repository.RoleRepository;
import com.infracore.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public RegistrationResponse register(RegistrationRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }
        
        if (userService.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);
        user.setActive(true);

        // Assign default USER role
        Role userRole = roleRepository.findByName(Role.RoleName.USER)
                .orElseThrow(() -> new RuntimeException("Default USER role not found"));
        user.addRole(userRole);

        User savedUser = userService.createUser(user);
        
        UserDTO userDTO = new UserDTO();
        userDTO.setId(savedUser.getId());
        userDTO.setUsername(savedUser.getUsername());
        userDTO.setEmail(savedUser.getEmail());
        userDTO.setFirstName(savedUser.getFirstName());
        userDTO.setLastName(savedUser.getLastName());
        
        return RegistrationResponse.builder()
                .success(true)
                .message("User registered successfully")
                .user(userDTO)
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtTokenProvider.generateToken(authentication);
        
        User user = userService.findByUsername(request.getUsername());
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        
        return LoginResponse.builder()
                .success(true)
                .message("Login successful")
                .accessToken(token)
                .user(userDTO)
                .build();
    }

    @Transactional
    public void createAdminUser() {
        if (!userService.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setEmail("admin@lawportal.com");
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEnabled(true);
            adminUser.setActive(true);
            
            // Assign ADMIN role
            Role adminRole = roleRepository.findByName(Role.RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            adminUser.addRole(adminRole);
            
            userService.createUser(adminUser);
        }
    }

    @Transactional
    public void initializeRoles() {
        // Create default roles if they don't exist
        for (Role.RoleName roleName : Role.RoleName.values()) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                role.setDescription(roleName.name() + " role");
                role.setIsActive(true);
                roleRepository.save(role);
            }
        }
    }
} 