package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.UserProfileDto;
import com.codewithudo.backend.dto.UserRegistrationDto;
import com.codewithudo.backend.entity.User;
import com.codewithudo.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(user.getRole().name())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
    }
    
    public UserProfileDto registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        
        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setIndustry(registrationDto.getIndustry());
        user.setSkills(registrationDto.getSkills());
        user.setInterests(registrationDto.getInterests());
        user.setLinkedinUrl(registrationDto.getLinkedinUrl());
        user.setCompanyId(registrationDto.getCompanyId());
        user.setRole(User.UserRole.USER);
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        return UserProfileDto.fromUser(savedUser);
    }
    
    public Optional<UserProfileDto> getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .map(UserProfileDto::fromUser);
    }
    
    public Optional<UserProfileDto> getUserProfileByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .map(UserProfileDto::fromUser);
    }
    
    public List<UserProfileDto> getAllActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::getIsActive)
                .map(UserProfileDto::fromUser)
                .toList();
    }
    
    public List<UserProfileDto> getUsersByIndustry(String industry) {
        return userRepository.findByIndustryAndIsActiveTrue(industry).stream()
                .map(UserProfileDto::fromUser)
                .toList();
    }
    
    public UserProfileDto updateUserProfile(Long userId, UserProfileDto profileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (profileDto.getName() != null) {
            user.setName(profileDto.getName());
        }
        if (profileDto.getIndustry() != null) {
            user.setIndustry(profileDto.getIndustry());
        }
        if (profileDto.getSkills() != null) {
            user.setSkills(profileDto.getSkills());
        }
        if (profileDto.getInterests() != null) {
            user.setInterests(profileDto.getInterests());
        }
        if (profileDto.getLinkedinUrl() != null) {
            user.setLinkedinUrl(profileDto.getLinkedinUrl());
        }
        
        User updatedUser = userRepository.save(user);
        return UserProfileDto.fromUser(updatedUser);
    }
    
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }
}
