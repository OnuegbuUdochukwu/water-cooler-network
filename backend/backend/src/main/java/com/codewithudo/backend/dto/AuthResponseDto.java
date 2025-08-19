package com.codewithudo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    
    private String token;
    private String tokenType = "Bearer";
    private UserProfileDto user;
    private String message;
    
    public AuthResponseDto(String token, UserProfileDto user, String message) {
        this.token = token;
        this.user = user;
        this.message = message;
    }
}
