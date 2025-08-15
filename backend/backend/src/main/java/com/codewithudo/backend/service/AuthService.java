package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.AuthResponseDto;
import com.codewithudo.backend.dto.UserLoginDto;
import com.codewithudo.backend.dto.UserProfileDto;
import com.codewithudo.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserService userService;
    
    public AuthResponseDto login(UserLoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = tokenProvider.generateToken(authentication);
        UserProfileDto userProfile = userService.getUserProfileByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        
        return new AuthResponseDto(jwt, userProfile, "Login successful");
    }
    
    public UserProfileDto getCurrentUser() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserProfileByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}
