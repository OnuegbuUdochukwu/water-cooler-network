package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.LoungeMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.codewithudo.backend.entity.LoungeMessage;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoungeMessageRepository extends JpaRepository<LoungeMessage, Long> {
    
    List<LoungeMessage> findByLoungeIdAndIsDeletedFalseOrderByCreatedAtAsc(Long loungeId);
    
    List<LoungeMessage> findByLoungeIdAndCreatedAtAfterAndIsDeletedFalseOrderByCreatedAtAsc(Long loungeId, LocalDateTime since);
    
    @Query("SELECT lm FROM LoungeMessage lm WHERE lm.loungeId = :loungeId AND lm.isDeleted = false ORDER BY lm.createdAt DESC")
    List<LoungeMessage> findRecentMessagesByLoungeId(@Param("loungeId") Long loungeId, Pageable pageable);
    
    List<LoungeMessage> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT lm FROM LoungeMessage lm WHERE lm.loungeId = :loungeId AND lm.messageType = 'TEXT' AND lm.isDeleted = false ORDER BY lm.createdAt DESC")
    List<LoungeMessage> findTextMessagesByLoungeId(@Param("loungeId") Long loungeId);
    
    @Query("SELECT lm FROM LoungeMessage lm WHERE lm.loungeId = :loungeId AND lm.messageType != 'TEXT' AND lm.isDeleted = false ORDER BY lm.createdAt DESC")
    List<LoungeMessage> findSystemMessagesByLoungeId(@Param("loungeId") Long loungeId);
    
    List<LoungeMessage> findByReplyToMessageIdAndIsDeletedFalse(Long replyToMessageId);
    
    @Query("SELECT COUNT(lm) FROM LoungeMessage lm WHERE lm.loungeId = :loungeId")
    Long countByLoungeId(@Param("loungeId") Long loungeId);
    
    Long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
