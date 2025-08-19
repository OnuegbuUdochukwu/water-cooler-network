package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.ConversationStarter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationStarterRepository extends JpaRepository<ConversationStarter, Long> {
    
    List<ConversationStarter> findByIsActiveTrue();
    
    List<ConversationStarter> findByContextTypeAndIsActiveTrue(ConversationStarter.ContextType contextType);
    
    List<ConversationStarter> findByCategoryAndIsActiveTrue(String category);
    
    @Query("SELECT cs FROM ConversationStarter cs WHERE cs.isActive = true AND " +
           "(cs.tags LIKE %:tag1% OR cs.tags LIKE %:tag2% OR cs.tags LIKE %:tag3%) " +
           "ORDER BY cs.successRate DESC, cs.usageCount ASC")
    List<ConversationStarter> findByTagsContaining(@Param("tag1") String tag1, 
                                                  @Param("tag2") String tag2, 
                                                  @Param("tag3") String tag3);
    
    @Query("SELECT cs FROM ConversationStarter cs WHERE cs.isActive = true AND cs.difficultyLevel <= :maxLevel " +
           "ORDER BY cs.successRate DESC LIMIT :limit")
    List<ConversationStarter> findTopByDifficultyLevel(@Param("maxLevel") Integer maxLevel, 
                                                      @Param("limit") Integer limit);
    
    @Query("SELECT cs FROM ConversationStarter cs WHERE cs.isActive = true " +
           "ORDER BY RAND() LIMIT :count")
    List<ConversationStarter> findRandomStarters(@Param("count") Integer count);
    
    @Query("SELECT cs FROM ConversationStarter cs WHERE cs.isActive = true AND cs.contextType = :contextType " +
           "ORDER BY cs.successRate DESC LIMIT :limit")
    List<ConversationStarter> findTopByContextType(@Param("contextType") ConversationStarter.ContextType contextType, 
                                                  @Param("limit") Integer limit);
}
