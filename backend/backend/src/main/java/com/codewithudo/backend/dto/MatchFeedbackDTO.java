package com.codewithudo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchFeedbackDTO {
    private Long id;
    private Long matchId;
    private Long userId;
    private Integer qualityRating;
    private Integer conversationRating;
    private Integer relevanceRating;
    private Boolean wouldMeetAgain;
    private String feedbackText;
    private String improvementSuggestions;
    private String tags;
    private LocalDateTime createdAt;
}
