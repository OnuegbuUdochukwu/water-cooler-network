package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.MentorshipSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MentorshipSessionRepository extends JpaRepository<MentorshipSession, Long> {

    List<MentorshipSession> findByRelationshipId(Long relationshipId);

    List<MentorshipSession> findByRelationshipIdOrderBySessionDateDesc(Long relationshipId);

    List<MentorshipSession> findByStatus(MentorshipSession.Status status);

    List<MentorshipSession> findByRelationshipIdAndStatus(Long relationshipId, MentorshipSession.Status status);

    List<MentorshipSession> findBySessionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<MentorshipSession> findByRelationshipIdAndSessionDateBetween(
            Long relationshipId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT s FROM MentorshipSession s WHERE s.relationship.mentorId = :mentorId " +
           "AND s.sessionDate >= :startDate AND s.sessionDate <= :endDate")
    List<MentorshipSession> findMentorSessionsByDateRange(
            @Param("mentorId") Long mentorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM MentorshipSession s WHERE s.relationship.menteeId = :menteeId " +
           "AND s.sessionDate >= :startDate AND s.sessionDate <= :endDate")
    List<MentorshipSession> findMenteeSessionsByDateRange(
            @Param("menteeId") Long menteeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM MentorshipSession s WHERE s.relationship.program.id = :programId " +
           "AND s.sessionDate >= :startDate AND s.sessionDate <= :endDate")
    List<MentorshipSession> findProgramSessionsByDateRange(
            @Param("programId") Long programId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM MentorshipSession s WHERE s.relationship.mentorId = :mentorId " +
           "AND s.status = 'SCHEDULED' AND s.sessionDate >= :currentDate " +
           "ORDER BY s.sessionDate ASC")
    List<MentorshipSession> findUpcomingMentorSessions(
            @Param("mentorId") Long mentorId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT s FROM MentorshipSession s WHERE s.relationship.menteeId = :menteeId " +
           "AND s.status = 'SCHEDULED' AND s.sessionDate >= :currentDate " +
           "ORDER BY s.sessionDate ASC")
    List<MentorshipSession> findUpcomingMenteeSessions(
            @Param("menteeId") Long menteeId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT COUNT(s) FROM MentorshipSession s WHERE s.relationship.mentorId = :mentorId " +
           "AND s.status = 'COMPLETED'")
    Long countCompletedMentorSessions(@Param("mentorId") Long mentorId);

    @Query("SELECT COUNT(s) FROM MentorshipSession s WHERE s.relationship.menteeId = :menteeId " +
           "AND s.status = 'COMPLETED'")
    Long countCompletedMenteeSessions(@Param("menteeId") Long menteeId);

    @Query("SELECT AVG(s.durationMinutes) FROM MentorshipSession s WHERE s.relationship.mentorId = :mentorId " +
           "AND s.status = 'COMPLETED' AND s.durationMinutes IS NOT NULL")
    Double getAverageMentorSessionDuration(@Param("mentorId") Long mentorId);

    @Query("SELECT AVG(s.durationMinutes) FROM MentorshipSession s WHERE s.relationship.menteeId = :menteeId " +
           "AND s.status = 'COMPLETED' AND s.durationMinutes IS NOT NULL")
    Double getAverageMenteeSessionDuration(@Param("menteeId") Long menteeId);
}
