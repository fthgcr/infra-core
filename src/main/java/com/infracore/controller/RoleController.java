package com.infracore.controller;

import com.infracore.entity.Role;
import com.infracore.entity.User;
import com.infracore.repository.RoleRepository;
import com.infracore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class RoleController {

    private final RoleRepository roleRepository;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> assignRole(
            @RequestParam String username, 
            @RequestParam String roleName) {
        
        try {
            User user = userService.findByUsername(username);
            Role role = roleRepository.findByName(Role.RoleName.valueOf(roleName))
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            
            user.addRole(role);
            userService.saveUser(user);
            
            return ResponseEntity.ok("Role " + roleName + " assigned to user " + username + " successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error assigning role: " + e.getMessage());
        }
    }

    @PostMapping("/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> removeRole(
            @RequestParam String username, 
            @RequestParam String roleName) {
        
        try {
            User user = userService.findByUsername(username);
            Role role = roleRepository.findByName(Role.RoleName.valueOf(roleName))
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            
            user.removeRole(role);
            userService.saveUser(user);
            
            return ResponseEntity.ok("Role " + roleName + " removed from user " + username + " successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error removing role: " + e.getMessage());
        }
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserRoles(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            return ResponseEntity.ok(user.getRoleNames());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching user roles: " + e.getMessage());
        }
    }
} 