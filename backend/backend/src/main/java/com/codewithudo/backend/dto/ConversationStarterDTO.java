package com.codewithudo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationStarterDTO {
    private Long id;
    private String template;
    private String category;
    private String contextType;
    private Integer difficultyLevel;
    private Double successRate;
    private Long usageCount;
}
