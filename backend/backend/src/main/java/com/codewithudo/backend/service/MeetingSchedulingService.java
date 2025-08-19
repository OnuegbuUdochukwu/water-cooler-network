package com.codewithudo.backend.service;

import com.codewithudo.backend.entity.Match;
import com.codewithudo.backend.entity.ScheduledMeeting;
import com.codewithudo.backend.entity.User;
import com.codewithudo.backend.repository.MatchRepository;
import com.codewithudo.backend.repository.ScheduledMeetingRepository;
import com.codewithudo.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingSchedulingService {
    
    private final ScheduledMeetingRepository scheduledMeetingRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final ConversationStarterService conversationStarterService;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public ScheduledMeeting scheduleMeeting(Long matchId, Long organizerId, 
                                          LocalDateTime startTime, LocalDateTime endTime,
                                          ScheduledMeeting.MeetingType meetingType, String location) {
        
        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new RuntimeException("Match not found"));
        
        User organizer = userRepository.findById(organizerId)
            .orElseThrow(() -> new RuntimeException("Organizer not found"));
        
        Long participantId = match.getUser1Id().equals(organizerId) ? 
            match.getUser2Id() : match.getUser1Id();
        
        User participant = userRepository.findById(participantId)
            .orElseThrow(() -> new RuntimeException("Participant not found"));
        
        // Check for scheduling conflicts
        if (hasSchedulingConflict(organizerId, startTime, endTime) ||
            hasSchedulingConflict(participantId, startTime, endTime)) {
            throw new RuntimeException("Scheduling conflict detected");
        }
        
        ScheduledMeeting meeting = new ScheduledMeeting();
        meeting.setMatchId(matchId);
        meeting.setOrganizerId(organizerId);
        meeting.setParticipantId(participantId);
        meeting.setMeetingTitle(generateMeetingTitle(organizer, participant));
        meeting.setMeetingDescription(generateMeetingDescription(organizer, participant));
        meeting.setScheduledStartTime(startTime);
        meeting.setScheduledEndTime(endTime);
        meeting.setTimeZone("UTC"); // Default timezone
        meeting.setMeetingType(meetingType);
        meeting.setMeetingLocation(location);
        meeting.setStatus(ScheduledMeeting.MeetingStatus.SCHEDULED);
        
        // Generate conversation starters for the meeting
        List<String> starters = conversationStarterService
            .generatePersonalizedStarters(organizerId, participantId, 5)
            .stream()
            .map(starter -> starter.getTemplate())
            .collect(Collectors.toList());
        
        try {
            meeting.setConversationStarters(objectMapper.writeValueAsString(starters));
        } catch (Exception e) {
            log.error("Error serializing conversation starters", e);
            meeting.setConversationStarters("[]");
        }
        
        ScheduledMeeting savedMeeting = scheduledMeetingRepository.save(meeting);
        
        // Update match status
        match.setStatus(Match.MatchStatus.SCHEDULED);
        match.setScheduledTime(startTime);
        matchRepository.save(match);
        
        log.info("Meeting scheduled: {} between users {} and {}", 
                savedMeeting.getId(), organizerId, participantId);
        
        return savedMeeting;
    }
    
    private boolean hasSchedulingConflict(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        List<ScheduledMeeting> existingMeetings = scheduledMeetingRepository
            .findUserMeetingsInRange(userId, startTime.minusMinutes(30), endTime.plusMinutes(30));
        
        return !existingMeetings.isEmpty();
    }
    
    private String generateMeetingTitle(User organizer, User participant) {
        return String.format("Coffee Chat: %s & %s", organizer.getName(), participant.getName());
    }
    
    private String generateMeetingDescription(User organizer, User participant) {
        StringBuilder description = new StringBuilder();
        description.append("Water Cooler Network Coffee Chat\n\n");
        description.append("Participants:\n");
        description.append("• ").append(organizer.getName());
        if (organizer.getIndustry() != null) {
            description.append(" (").append(organizer.getIndustry()).append(")");
        }
        description.append("\n");
        description.append("• ").append(participant.getName());
        if (participant.getIndustry() != null) {
            description.append(" (").append(participant.getIndustry()).append(")");
        }
        description.append("\n\n");
        description.append("This is a networking opportunity to connect, share experiences, and learn from each other.");
        
        return description.toString();
    }
    
    @Transactional
    public ScheduledMeeting rescheduleMeeting(Long meetingId, LocalDateTime newStartTime, 
                                            LocalDateTime newEndTime) {
        ScheduledMeeting meeting = scheduledMeetingRepository.findById(meetingId)
            .orElseThrow(() -> new RuntimeException("Meeting not found"));
        
        // Check for conflicts with new time
        if (hasSchedulingConflict(meeting.getOrganizerId(), newStartTime, newEndTime) ||
            hasSchedulingConflict(meeting.getParticipantId(), newStartTime, newEndTime)) {
            throw new RuntimeException("Scheduling conflict with new time");
        }
        
        meeting.setScheduledStartTime(newStartTime);
        meeting.setScheduledEndTime(newEndTime);
        meeting.setStatus(ScheduledMeeting.MeetingStatus.RESCHEDULED);
        meeting.setReminderSent(false); // Reset reminder flag
        
        return scheduledMeetingRepository.save(meeting);
    }
    
    @Transactional
    public void cancelMeeting(Long meetingId, String reason) {
        ScheduledMeeting meeting = scheduledMeetingRepository.findById(meetingId)
            .orElseThrow(() -> new RuntimeException("Meeting not found"));
        
        meeting.setStatus(ScheduledMeeting.MeetingStatus.CANCELLED);
        meeting.setMeetingNotes(reason);
        
        scheduledMeetingRepository.save(meeting);
        
        // Update match status back to accepted
        Match match = matchRepository.findById(meeting.getMatchId()).orElse(null);
        if (match != null) {
            match.setStatus(Match.MatchStatus.ACCEPTED);
            matchRepository.save(match);
        }
    }
    
    @Transactional
    public void startMeeting(Long meetingId) {
        ScheduledMeeting meeting = scheduledMeetingRepository.findById(meetingId)
            .orElseThrow(() -> new RuntimeException("Meeting not found"));
        
        meeting.setStatus(ScheduledMeeting.MeetingStatus.IN_PROGRESS);
        meeting.setActualStartTime(LocalDateTime.now());
        
        scheduledMeetingRepository.save(meeting);
        
        // Update match status
        Match match = matchRepository.findById(meeting.getMatchId()).orElse(null);
        if (match != null) {
            match.setStatus(Match.MatchStatus.IN_PROGRESS);
            matchRepository.save(match);
        }
    }
    
    @Transactional
    public void completeMeeting(Long meetingId, String notes) {
        ScheduledMeeting meeting = scheduledMeetingRepository.findById(meetingId)
            .orElseThrow(() -> new RuntimeException("Meeting not found"));
        
        meeting.setStatus(ScheduledMeeting.MeetingStatus.COMPLETED);
        meeting.setActualEndTime(LocalDateTime.now());
        meeting.setMeetingNotes(notes);
        
        scheduledMeetingRepository.save(meeting);
        
        // Update match status
        Match match = matchRepository.findById(meeting.getMatchId()).orElse(null);
        if (match != null) {
            match.setStatus(Match.MatchStatus.COMPLETED);
            matchRepository.save(match);
        }
    }
    
    public List<ScheduledMeeting> getUserMeetings(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return scheduledMeetingRepository.findUserMeetingsInRange(userId, startDate, endDate);
    }
    
    public List<ScheduledMeeting> getUpcomingMeetings(Long userId, int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(days);
        return scheduledMeetingRepository.findUserMeetingsInRange(userId, now, future);
    }
    
    public List<TimeSlot> suggestMeetingTimes(Long user1Id, Long user2Id, int durationMinutes, int suggestions) {
        // Simple time suggestion algorithm
        // In production, this would integrate with calendar APIs
        
        List<TimeSlot> suggestions_list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Get existing meetings for both users
        List<ScheduledMeeting> user1Meetings = getUserMeetings(user1Id, now, now.plusDays(14));
        List<ScheduledMeeting> user2Meetings = getUserMeetings(user2Id, now, now.plusDays(14));
        
        // Combine all busy times
        Set<TimeSlot> busySlots = new HashSet<>();
        user1Meetings.forEach(meeting -> busySlots.add(
            new TimeSlot(meeting.getScheduledStartTime(), meeting.getScheduledEndTime())));
        user2Meetings.forEach(meeting -> busySlots.add(
            new TimeSlot(meeting.getScheduledStartTime(), meeting.getScheduledEndTime())));
        
        // Generate suggestions for next 14 days, business hours only
        for (int day = 1; day <= 14 && suggestions_list.size() < suggestions; day++) {
            LocalDateTime dayStart = now.plusDays(day).withHour(9).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = dayStart.withHour(17);
            
            // Skip weekends
            if (dayStart.getDayOfWeek().getValue() > 5) continue;
            
            // Check hourly slots
            for (LocalDateTime slotStart = dayStart; 
                 slotStart.plusMinutes(durationMinutes).isBefore(dayEnd) && suggestions_list.size() < suggestions; 
                 slotStart = slotStart.plusHours(1)) {
                
                LocalDateTime slotEnd = slotStart.plusMinutes(durationMinutes);
                TimeSlot proposedSlot = new TimeSlot(slotStart, slotEnd);
                
                // Check if slot conflicts with existing meetings
                boolean hasConflict = busySlots.stream()
                    .anyMatch(busySlot -> proposedSlot.overlapsWith(busySlot));
                
                if (!hasConflict) {
                    suggestions_list.add(proposedSlot);
                }
            }
        }
        
        return suggestions_list;
    }
    
    // Scheduled task to send meeting reminders
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @Transactional
    public void sendMeetingReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderWindow = now.plusHours(1); // 1 hour before meeting
        
        List<ScheduledMeeting> meetingsNeedingReminders = scheduledMeetingRepository
            .findMeetingsNeedingReminders(now, reminderWindow);
        
        for (ScheduledMeeting meeting : meetingsNeedingReminders) {
            // In production, this would send actual notifications/emails
            log.info("Sending reminder for meeting {} scheduled at {}", 
                    meeting.getId(), meeting.getScheduledStartTime());
            
            meeting.setReminderSent(true);
            scheduledMeetingRepository.save(meeting);
        }
    }
    
    // Inner class for time slot management
    public static class TimeSlot {
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;
        
        public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        public boolean overlapsWith(TimeSlot other) {
            return startTime.isBefore(other.endTime) && endTime.isAfter(other.startTime);
        }
        
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TimeSlot)) return false;
            TimeSlot timeSlot = (TimeSlot) o;
            return Objects.equals(startTime, timeSlot.startTime) && Objects.equals(endTime, timeSlot.endTime);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(startTime, endTime);
        }
    }
}
