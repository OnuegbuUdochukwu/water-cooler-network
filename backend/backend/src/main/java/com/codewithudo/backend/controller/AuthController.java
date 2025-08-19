package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.AuthResponseDto;
import com.codewithudo.backend.dto.UserLoginDto;
import com.codewithudo.backend.dto.UserProfileDto;
import com.codewithudo.backend.dto.UserRegistrationDto;
import com.codewithudo.backend.service.AuthService;
import com.codewithudo.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            UserProfileDto userProfile = userService.registerUser(registrationDto);
            // For registration, we'll return a success message without a token
            // User will need to login to get a token
            return ResponseEntity.ok(new AuthResponseDto(null, userProfile, "Registration successful. Please login."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponseDto(null, null, e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody UserLoginDto loginDto) {
        try {
            AuthResponseDto response = authService.login(loginDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponseDto(null, null, "Invalid credentials"));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser() {
        try {
            UserProfileDto currentUser = authService.getCurrentUser();
            return ResponseEntity.ok(currentUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
