package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.Lounge;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LoungeDto {
    
    private Long id;
    private String title;
    private String description;
    private String topic;
    private String category;
    private String tags;
    private Long createdBy;
    private String creatorName;
    private String creatorEmail;
    private Lounge.Visibility visibility;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Boolean isActive;
    private Boolean isFeatured;
    private LocalDateTime lastActivity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> tagList;
    private Boolean isParticipant;
    private String participantRole;
}
