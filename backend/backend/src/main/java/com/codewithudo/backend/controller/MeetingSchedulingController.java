package com.codewithudo.backend.controller;

import com.codewithudo.backend.dto.ScheduledMeetingDTO;
import com.codewithudo.backend.dto.TimeSlotDTO;
import com.codewithudo.backend.entity.ScheduledMeeting;
import com.codewithudo.backend.entity.User;
import com.codewithudo.backend.service.MeetingSchedulingService;
import com.codewithudo.backend.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class MeetingSchedulingController {
    
    private final MeetingSchedulingService meetingSchedulingService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    
    @PostMapping("/schedule")
    public ResponseEntity<ScheduledMeetingDTO> scheduleMeeting(
            Authentication authentication,
            @RequestParam Long matchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam String meetingType,
            @RequestParam(required = false) String location) {
        
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        ScheduledMeeting.MeetingType type = ScheduledMeeting.MeetingType.valueOf(meetingType.toUpperCase());
        
        ScheduledMeeting meeting = meetingSchedulingService.scheduleMeeting(
            matchId, currentUser.getId(), startTime, endTime, type, location);
        
        return ResponseEntity.ok(convertToDTO(meeting));
    }
    
    @PutMapping("/{meetingId}/reschedule")
    public ResponseEntity<ScheduledMeetingDTO> rescheduleMeeting(
            @PathVariable Long meetingId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newEndTime) {
        
        ScheduledMeeting meeting = meetingSchedulingService.rescheduleMeeting(
            meetingId, newStartTime, newEndTime);
        
        return ResponseEntity.ok(convertToDTO(meeting));
    }
    
    @PutMapping("/{meetingId}/cancel")
    public ResponseEntity<Void> cancelMeeting(
            @PathVariable Long meetingId,
            @RequestParam(required = false) String reason) {
        
        meetingSchedulingService.cancelMeeting(meetingId, reason);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{meetingId}/start")
    public ResponseEntity<Void> startMeeting(@PathVariable Long meetingId) {
        meetingSchedulingService.startMeeting(meetingId);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{meetingId}/complete")
    public ResponseEntity<Void> completeMeeting(
            @PathVariable Long meetingId,
            @RequestParam(required = false) String notes) {
        
        meetingSchedulingService.completeMeeting(meetingId, notes);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/my-meetings")
    public ResponseEntity<List<ScheduledMeetingDTO>> getMyMeetings(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now();
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now().plusDays(30);
        
        List<ScheduledMeeting> meetings = meetingSchedulingService
            .getUserMeetings(currentUser.getId(), start, end);
        
        List<ScheduledMeetingDTO> meetingDTOs = meetings.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(meetingDTOs);
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<ScheduledMeetingDTO>> getUpcomingMeetings(
            Authentication authentication,
            @RequestParam(defaultValue = "7") int days) {
        
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        List<ScheduledMeeting> meetings = meetingSchedulingService
            .getUpcomingMeetings(currentUser.getId(), days);
        
        List<ScheduledMeetingDTO> meetingDTOs = meetings.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(meetingDTOs);
    }
    
    @GetMapping("/suggest-times")
    public ResponseEntity<List<TimeSlotDTO>> suggestMeetingTimes(
            Authentication authentication,
            @RequestParam Long otherUserId,
            @RequestParam(defaultValue = "30") int durationMinutes,
            @RequestParam(defaultValue = "5") int suggestions) {
        
        String email = authentication.getName();
        User currentUser = userService.findByEmail(email);
        
        List<MeetingSchedulingService.TimeSlot> timeSlots = meetingSchedulingService
            .suggestMeetingTimes(currentUser.getId(), otherUserId, durationMinutes, suggestions);
        
        List<TimeSlotDTO> timeSlotDTOs = timeSlots.stream()
            .map(this::convertToTimeSlotDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(timeSlotDTOs);
    }
    
    private ScheduledMeetingDTO convertToDTO(ScheduledMeeting meeting) {
        ScheduledMeetingDTO dto = new ScheduledMeetingDTO();
        dto.setId(meeting.getId());
        dto.setMatchId(meeting.getMatchId());
        dto.setOrganizerId(meeting.getOrganizerId());
        dto.setParticipantId(meeting.getParticipantId());
        dto.setMeetingTitle(meeting.getMeetingTitle());
        dto.setMeetingDescription(meeting.getMeetingDescription());
        dto.setScheduledStartTime(meeting.getScheduledStartTime());
        dto.setScheduledEndTime(meeting.getScheduledEndTime());
        dto.setTimeZone(meeting.getTimeZone());
        dto.setMeetingType(meeting.getMeetingType().name());
        dto.setMeetingLocation(meeting.getMeetingLocation());
        dto.setStatus(meeting.getStatus().name());
        dto.setMeetingNotes(meeting.getMeetingNotes());
        
        // Parse conversation starters
        try {
            if (meeting.getConversationStarters() != null) {
                List<String> starters = objectMapper.readValue(
                    meeting.getConversationStarters(), new TypeReference<List<String>>() {});
                dto.setConversationStarters(starters);
            }
        } catch (Exception e) {
            dto.setConversationStarters(List.of());
        }
        
        // Get user names
        try {
            User organizer = userService.findById(meeting.getOrganizerId());
            User participant = userService.findById(meeting.getParticipantId());
            dto.setOrganizerName(organizer.getName());
            dto.setParticipantName(participant.getName());
        } catch (Exception e) {
            // Handle gracefully
        }
        
        return dto;
    }
    
    private TimeSlotDTO convertToTimeSlotDTO(MeetingSchedulingService.TimeSlot timeSlot) {
        TimeSlotDTO dto = new TimeSlotDTO();
        dto.setStartTime(timeSlot.getStartTime());
        dto.setEndTime(timeSlot.getEndTime());
        dto.setIsAvailable(true);
        
        // Format display text
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' h:mm a");
        dto.setDisplayText(timeSlot.getStartTime().format(formatter));
        
        return dto;
    }
}
