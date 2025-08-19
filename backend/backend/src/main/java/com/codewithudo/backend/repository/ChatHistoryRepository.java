package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    
    List<ChatHistory> findByMatchIdOrderByTimestampAsc(Long matchId);
    
    List<ChatHistory> findByMatchIdAndTimestampBetweenOrderByTimestampAsc(Long matchId, LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT ch FROM ChatHistory ch WHERE ch.matchId = :matchId AND ch.messageType = 'TEXT' ORDER BY ch.timestamp ASC")
    List<ChatHistory> findTextMessagesByMatchId(@Param("matchId") Long matchId);
    
    @Query("SELECT ch FROM ChatHistory ch WHERE ch.matchId = :matchId AND ch.isSystemMessage = true ORDER BY ch.timestamp ASC")
    List<ChatHistory> findSystemMessagesByMatchId(@Param("matchId") Long matchId);
    
    List<ChatHistory> findByUserIdAndTimestampBetweenOrderByTimestampDesc(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
