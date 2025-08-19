package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.AuthResponseDto;
import com.codewithudo.backend.dto.UserLoginDto;
import com.codewithudo.backend.dto.UserProfileDto;
import com.codewithudo.backend.entity.ActivityLog;
import com.codewithudo.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private GamificationService gamificationService;
    
    public AuthResponseDto login(UserLoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String jwt = tokenProvider.generateToken(authentication);
        UserProfileDto userProfile = userService.getUserProfileByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        
        // Log login activity for gamification
        gamificationService.logActivity(userProfile.getId(), ActivityLog.ActivityType.LOGIN, null, null);
        
        return new AuthResponseDto(jwt, userProfile, "Login successful");
    }
    
    public UserProfileDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("No authenticated user");
        }
        Object principal = authentication.getPrincipal();
        String currentUserEmail;
        if (principal instanceof UserDetails userDetails) {
            currentUserEmail = userDetails.getUsername();
        } else if (principal instanceof String s) {
            currentUserEmail = s;
        } else {
            throw new RuntimeException("Unrecognized principal type: " + principal.getClass().getName());
        }
        return userService.getUserProfileByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}
