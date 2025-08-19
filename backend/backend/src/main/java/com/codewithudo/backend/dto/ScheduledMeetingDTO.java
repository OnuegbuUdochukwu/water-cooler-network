package com.codewithudo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMeetingDTO {
    private Long id;
    private Long matchId;
    private Long organizerId;
    private String organizerName;
    private Long participantId;
    private String participantName;
    private String meetingTitle;
    private String meetingDescription;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;
    private String timeZone;
    private String meetingType;
    private String meetingLocation;
    private String status;
    private List<String> conversationStarters;
    private String meetingNotes;
}
