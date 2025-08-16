package com.codewithudo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartMatchDTO {
    private Long userId;
    private String name;
    private String email;
    private String industry;
    private String skills;
    private String interests;
    private String linkedinUrl;
    private Double compatibilityScore;
    private Map<String, Double> compatibilityFactors;
    private String matchReason;
}
