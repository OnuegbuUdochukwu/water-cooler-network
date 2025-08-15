package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.LoungeParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoungeParticipantRepository extends JpaRepository<LoungeParticipant, Long> {
    
    List<LoungeParticipant> findByLoungeIdAndIsActiveTrue(Long loungeId);
    
    List<LoungeParticipant> findByUserIdAndIsActiveTrue(Long userId);
    
    Optional<LoungeParticipant> findByLoungeIdAndUserIdAndIsActiveTrue(Long loungeId, Long userId);
    
    boolean existsByLoungeIdAndUserIdAndIsActiveTrue(Long loungeId, Long userId);
    
    @Query("SELECT lp FROM LoungeParticipant lp WHERE lp.loungeId = :loungeId AND lp.role IN ('CREATOR', 'MODERATOR') AND lp.isActive = true")
    List<LoungeParticipant> findModeratorsByLoungeId(@Param("loungeId") Long loungeId);
    
    @Query("SELECT COUNT(lp) FROM LoungeParticipant lp WHERE lp.loungeId = :loungeId AND lp.isActive = true")
    Long countActiveParticipantsByLoungeId(@Param("loungeId") Long loungeId);
    
    @Query("SELECT lp FROM LoungeParticipant lp WHERE lp.loungeId = :loungeId AND lp.lastActivity < :since AND lp.isActive = true")
    List<LoungeParticipant> findInactiveParticipants(@Param("loungeId") Long loungeId, @Param("since") LocalDateTime since);
    
    List<LoungeParticipant> findByLoungeIdAndRoleAndIsActiveTrue(Long loungeId, LoungeParticipant.ParticipantRole role);
}
