package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.ScheduledMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledMeetingRepository extends JpaRepository<ScheduledMeeting, Long> {
    
    List<ScheduledMeeting> findByMatchId(Long matchId);
    
    List<ScheduledMeeting> findByOrganizerId(Long organizerId);
    
    List<ScheduledMeeting> findByParticipantId(Long participantId);
    
    @Query("SELECT sm FROM ScheduledMeeting sm WHERE (sm.organizerId = :userId OR sm.participantId = :userId) " +
           "AND sm.scheduledStartTime >= :startTime AND sm.scheduledStartTime <= :endTime")
    List<ScheduledMeeting> findUserMeetingsInRange(@Param("userId") Long userId, 
                                                  @Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT sm FROM ScheduledMeeting sm WHERE sm.status = :status AND sm.scheduledStartTime <= :time")
    List<ScheduledMeeting> findMeetingsByStatusAndTime(@Param("status") ScheduledMeeting.MeetingStatus status, 
                                                      @Param("time") LocalDateTime time);
    
    @Query("SELECT sm FROM ScheduledMeeting sm WHERE sm.reminderSent = false AND " +
           "sm.scheduledStartTime BETWEEN :now AND :reminderTime")
    List<ScheduledMeeting> findMeetingsNeedingReminders(@Param("now") LocalDateTime now, 
                                                       @Param("reminderTime") LocalDateTime reminderTime);
    
    @Query("SELECT COUNT(sm) FROM ScheduledMeeting sm WHERE (sm.organizerId = :userId OR sm.participantId = :userId) " +
           "AND sm.status = 'COMPLETED'")
    Long countCompletedMeetings(@Param("userId") Long userId);
    
    Long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
