package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.MentorshipProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MentorshipProgramRepository extends JpaRepository<MentorshipProgram, Long> {

    List<MentorshipProgram> findByCompanyIdAndIsActiveTrue(Long companyId);

    List<MentorshipProgram> findByCompanyId(Long companyId);

    List<MentorshipProgram> findByCompanyIdAndProgramTypeAndIsActiveTrue(
            Long companyId, MentorshipProgram.ProgramType programType);

    List<MentorshipProgram> findByCompanyIdAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(
            Long companyId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT p FROM MentorshipProgram p WHERE p.companyId = :companyId " +
           "AND p.isActive = true AND p.startDate <= :currentDate " +
           "AND (p.endDate IS NULL OR p.endDate >= :currentDate)")
    List<MentorshipProgram> findCurrentlyActivePrograms(
            @Param("companyId") Long companyId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT p FROM MentorshipProgram p WHERE p.companyId = :companyId " +
           "AND p.isActive = true AND p.startDate > :currentDate")
    List<MentorshipProgram> findUpcomingPrograms(
            @Param("companyId") Long companyId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT p FROM MentorshipProgram p WHERE p.companyId = :companyId " +
           "AND p.isActive = true AND p.endDate < :currentDate")
    List<MentorshipProgram> findCompletedPrograms(
            @Param("companyId") Long companyId, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT COUNT(p) FROM MentorshipProgram p WHERE p.companyId = :companyId AND p.isActive = true")
    Long countActiveProgramsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT p FROM MentorshipProgram p WHERE p.companyId = :companyId " +
           "AND p.isActive = true AND p.minMentorExperienceYears <= :experienceYears")
    List<MentorshipProgram> findProgramsByMentorExperience(
            @Param("companyId") Long companyId, @Param("experienceYears") Integer experienceYears);
}
