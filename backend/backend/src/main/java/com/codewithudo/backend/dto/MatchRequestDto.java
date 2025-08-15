package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.Match;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchRequestDto {
    
    private Long targetUserId;
    private Match.MatchType matchType = Match.MatchType.COFFEE_CHAT;
    private String message;
    private LocalDateTime preferredTime;
    private Integer durationMinutes = 30;
}
