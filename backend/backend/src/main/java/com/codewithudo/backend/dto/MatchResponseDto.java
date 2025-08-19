package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.Match;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchResponseDto {
    
    private Long matchId;
    private Match.MatchStatus status;
    private String message;
    private LocalDateTime scheduledTime;
    private Integer durationMinutes;
}
