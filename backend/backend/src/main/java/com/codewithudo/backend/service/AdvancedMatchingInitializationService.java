package com.codewithudo.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvancedMatchingInitializationService implements ApplicationRunner {
    
    private final ConversationStarterService conversationStarterService;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing Advanced Matching & AI components...");
        
        // Initialize conversation starters
        conversationStarterService.initializeDefaultStarters();
        
        log.info("Advanced Matching & AI initialization completed");
    }
}
