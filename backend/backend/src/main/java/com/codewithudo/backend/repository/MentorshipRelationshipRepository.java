package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.MentorshipRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MentorshipRelationshipRepository extends JpaRepository<MentorshipRelationship, Long> {

    List<MentorshipRelationship> findByMentorId(Long mentorId);

    List<MentorshipRelationship> findByMenteeId(Long menteeId);

    List<MentorshipRelationship> findByMentorIdOrMenteeId(Long mentorId, Long menteeId);

    List<MentorshipRelationship> findByProgram_Id(Long programId);

    List<MentorshipRelationship> findByStatus(MentorshipRelationship.Status status);

    List<MentorshipRelationship> findByMentorIdAndStatus(Long mentorId, MentorshipRelationship.Status status);

    List<MentorshipRelationship> findByMenteeIdAndStatus(Long menteeId, MentorshipRelationship.Status status);

    List<MentorshipRelationship> findByProgram_IdAndStatus(Long programId, MentorshipRelationship.Status status);

    @Query("SELECT r FROM MentorshipRelationship r WHERE r.mentorId = :mentorId " +
           "AND r.status = 'ACTIVE'")
    List<MentorshipRelationship> findActiveMentorRelationships(@Param("mentorId") Long mentorId);

    @Query("SELECT r FROM MentorshipRelationship r WHERE r.menteeId = :menteeId " +
           "AND r.status = 'ACTIVE'")
    List<MentorshipRelationship> findActiveMenteeRelationships(@Param("menteeId") Long menteeId);

    @Query("SELECT r FROM MentorshipRelationship r WHERE r.program.id = :programId " +
           "AND r.status = 'ACTIVE'")
    List<MentorshipRelationship> findActiveProgramRelationships(@Param("programId") Long programId);

    @Query("SELECT COUNT(r) FROM MentorshipRelationship r WHERE r.mentorId = :mentorId " +
           "AND r.status = 'ACTIVE'")
    Long countActiveMentorRelationships(@Param("mentorId") Long mentorId);

    @Query("SELECT COUNT(r) FROM MentorshipRelationship r WHERE r.menteeId = :menteeId " +
           "AND r.status = 'ACTIVE'")
    Long countActiveMenteeRelationships(@Param("menteeId") Long menteeId);

    @Query("SELECT r FROM MentorshipRelationship r WHERE r.mentorId = :mentorId " +
           "AND r.startDate >= :startDate AND r.startDate <= :endDate")
    List<MentorshipRelationship> findMentorRelationshipsByDateRange(
            @Param("mentorId") Long mentorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM MentorshipRelationship r WHERE r.menteeId = :menteeId " +
           "AND r.startDate >= :startDate AND r.startDate <= :endDate")
    List<MentorshipRelationship> findMenteeRelationshipsByDateRange(
            @Param("menteeId") Long menteeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM MentorshipRelationship r WHERE r.program.id = :programId " +
           "AND r.startDate >= :startDate AND r.startDate <= :endDate")
    List<MentorshipRelationship> findProgramRelationshipsByDateRange(
            @Param("programId") Long programId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
