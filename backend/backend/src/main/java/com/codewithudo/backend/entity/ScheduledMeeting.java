package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_meetings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledMeeting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "match_id", nullable = false)
    private Long matchId;
    
    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;
    
    @Column(name = "participant_id", nullable = false)
    private Long participantId;
    
    @Column(name = "meeting_title", nullable = false)
    private String meetingTitle;
    
    @Column(name = "meeting_description", columnDefinition = "TEXT")
    private String meetingDescription;
    
    @Column(name = "scheduled_start_time", nullable = false)
    private LocalDateTime scheduledStartTime;
    
    @Column(name = "scheduled_end_time", nullable = false)
    private LocalDateTime scheduledEndTime;
    
    @Column(name = "time_zone")
    private String timeZone;
    
    @Column(name = "meeting_type")
    @Enumerated(EnumType.STRING)
    private MeetingType meetingType;
    
    @Column(name = "meeting_location")
    private String meetingLocation; // Physical location or video link
    
    @Column(name = "calendar_event_id")
    private String calendarEventId; // External calendar integration ID
    
    @Column(name = "reminder_sent")
    private Boolean reminderSent = false;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MeetingStatus status = MeetingStatus.SCHEDULED;
    
    @Column(name = "conversation_starters", columnDefinition = "TEXT")
    private String conversationStarters; // JSON array of suggested topics
    
    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;
    
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;
    
    @Column(name = "meeting_notes", columnDefinition = "TEXT")
    private String meetingNotes;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum MeetingType {
        VIRTUAL, IN_PERSON, PHONE_CALL, COFFEE_CHAT
    }
    
    public enum MeetingStatus {
        SCHEDULED, CONFIRMED, RESCHEDULED, CANCELLED, IN_PROGRESS, COMPLETED, NO_SHOW
    }
}
