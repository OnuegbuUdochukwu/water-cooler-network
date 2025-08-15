package com.codewithudo.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {
    
    private final GamificationService gamificationService;
    private final BadgeService badgeService;
    
    // Run daily at 2 AM to reset inactive streaks
    @Scheduled(cron = "0 0 2 * * ?")
    public void resetInactiveStreaks() {
        log.info("Starting daily streak reset task");
        gamificationService.resetInactiveStreaks();
        log.info("Daily streak reset task completed");
    }
    
    // Initialize badges on startup if needed
    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void initializeBadgesOnStartup() {
        log.info("Initializing default badges on startup");
        badgeService.initializeDefaultBadges();
        log.info("Badge initialization completed");
    }
}
