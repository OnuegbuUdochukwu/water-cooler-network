package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.Match;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchDto {
    
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private String user1Name;
    private String user2Name;
    private String user1Email;
    private String user2Email;
    private Match.MatchType matchType;
    private Match.MatchStatus status;
    private LocalDateTime matchTime;
    private LocalDateTime scheduledTime;
    private Integer durationMinutes;
    private Double compatibilityScore;
    private String matchReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
