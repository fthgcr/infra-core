package com.infracore.controller;

import com.infracore.dto.LoginRequest;
import com.infracore.dto.LoginResponse;
import com.infracore.dto.RegistrationRequest;
import com.infracore.dto.RegistrationResponse;
import com.infracore.dto.JwtAuthenticationResponse;
import com.infracore.security.JwtTokenProvider;
import com.infracore.security.UserPrincipal;
import com.infracore.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest request) {
        RegistrationResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtTokenProvider.generateToken(userPrincipal);

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, userPrincipal.getPrimaryRole()));
    }

    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin() {
        authService.createAdminUser();
        return ResponseEntity.ok("Admin user created successfully");
    }
} 