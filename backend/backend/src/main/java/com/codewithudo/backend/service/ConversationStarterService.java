package com.codewithudo.backend.service;

import com.codewithudo.backend.entity.ConversationStarter;
import com.codewithudo.backend.entity.User;
import com.codewithudo.backend.repository.ConversationStarterRepository;
import com.codewithudo.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationStarterService {
    
    private final ConversationStarterRepository conversationStarterRepository;
    private final UserRepository userRepository;
    
    public List<ConversationStarter> generatePersonalizedStarters(Long user1Id, Long user2Id, int count) {
        log.info("Generating personalized conversation starters for users {} and {}", user1Id, user2Id);
        
        User user1 = userRepository.findById(user1Id)
            .orElseThrow(() -> new RuntimeException("User not found: " + user1Id));
        User user2 = userRepository.findById(user2Id)
            .orElseThrow(() -> new RuntimeException("User not found: " + user2Id));
        
        List<ConversationStarter> starters = new ArrayList<>();
        
        // Get skill-based starters
        starters.addAll(getSkillBasedStarters(user1, user2, count / 3));
        
        // Get interest-based starters
        starters.addAll(getInterestBasedStarters(user1, user2, count / 3));
        
        // Get industry-based starters
        starters.addAll(getIndustryBasedStarters(user1, user2, count / 3));
        
        // Fill remaining with general starters
        if (starters.size() < count) {
            starters.addAll(getGeneralStarters(count - starters.size()));
        }
        
        // Personalize templates
        return starters.stream()
            .limit(count)
            .map(starter -> personalizeStarter(starter, user1, user2))
            .collect(Collectors.toList());
    }
    
    private List<ConversationStarter> getSkillBasedStarters(User user1, User user2, int count) {
        Set<String> commonSkills = findCommonKeywords(user1.getSkills(), user2.getSkills());
        
        if (commonSkills.isEmpty()) {
            return conversationStarterRepository.findByContextTypeAndIsActiveTrue(
                ConversationStarter.ContextType.SKILL_BASED).stream()
                .limit(count)
                .collect(Collectors.toList());
        }
        
        List<String> skillTags = new ArrayList<>(commonSkills);
        String tag1 = skillTags.size() > 0 ? skillTags.get(0) : "";
        String tag2 = skillTags.size() > 1 ? skillTags.get(1) : "";
        String tag3 = skillTags.size() > 2 ? skillTags.get(2) : "";
        
        return conversationStarterRepository.findByTagsContaining(tag1, tag2, tag3)
            .stream()
            .limit(count)
            .collect(Collectors.toList());
    }
    
    private List<ConversationStarter> getInterestBasedStarters(User user1, User user2, int count) {
        Set<String> commonInterests = findCommonKeywords(user1.getInterests(), user2.getInterests());
        
        if (commonInterests.isEmpty()) {
            return conversationStarterRepository.findByContextTypeAndIsActiveTrue(
                ConversationStarter.ContextType.INTEREST_BASED).stream()
                .limit(count)
                .collect(Collectors.toList());
        }
        
        List<String> interestTags = new ArrayList<>(commonInterests);
        String tag1 = interestTags.size() > 0 ? interestTags.get(0) : "";
        String tag2 = interestTags.size() > 1 ? interestTags.get(1) : "";
        String tag3 = interestTags.size() > 2 ? interestTags.get(2) : "";
        
        return conversationStarterRepository.findByTagsContaining(tag1, tag2, tag3)
            .stream()
            .limit(count)
            .collect(Collectors.toList());
    }
    
    private List<ConversationStarter> getIndustryBasedStarters(User user1, User user2, int count) {
        if (user1.getIndustry() != null && user1.getIndustry().equalsIgnoreCase(user2.getIndustry())) {
            return conversationStarterRepository.findByContextTypeAndIsActiveTrue(
                ConversationStarter.ContextType.INDUSTRY_BASED).stream()
                .limit(count)
                .collect(Collectors.toList());
        }
        
        return conversationStarterRepository.findByContextTypeAndIsActiveTrue(
            ConversationStarter.ContextType.PROFESSIONAL).stream()
            .limit(count)
            .collect(Collectors.toList());
    }
    
    private List<ConversationStarter> getGeneralStarters(int count) {
        return conversationStarterRepository.findRandomStarters(count);
    }
    
    private Set<String> findCommonKeywords(String text1, String text2) {
        if (text1 == null || text2 == null) return Collections.emptySet();
        
        Set<String> words1 = Arrays.stream(text1.toLowerCase().split("[,\\s]+"))
            .map(String::trim)
            .filter(s -> !s.isEmpty() && s.length() > 2)
            .collect(Collectors.toSet());
        
        Set<String> words2 = Arrays.stream(text2.toLowerCase().split("[,\\s]+"))
            .map(String::trim)
            .filter(s -> !s.isEmpty() && s.length() > 2)
            .collect(Collectors.toSet());
        
        words1.retainAll(words2);
        return words1;
    }
    
    private ConversationStarter personalizeStarter(ConversationStarter starter, User user1, User user2) {
        String personalizedTemplate = starter.getTemplate();
        
        // Replace placeholders with actual user information
        personalizedTemplate = personalizedTemplate.replace("{user1_name}", user1.getName());
        personalizedTemplate = personalizedTemplate.replace("{user2_name}", user2.getName());
        personalizedTemplate = personalizedTemplate.replace("{user1_industry}", 
            user1.getIndustry() != null ? user1.getIndustry() : "your field");
        personalizedTemplate = personalizedTemplate.replace("{user2_industry}", 
            user2.getIndustry() != null ? user2.getIndustry() : "their field");
        
        // Create a copy with personalized template
        ConversationStarter personalized = new ConversationStarter();
        personalized.setId(starter.getId());
        personalized.setTemplate(personalizedTemplate);
        personalized.setCategory(starter.getCategory());
        personalized.setTags(starter.getTags());
        personalized.setContextType(starter.getContextType());
        personalized.setDifficultyLevel(starter.getDifficultyLevel());
        personalized.setUsageCount(starter.getUsageCount());
        personalized.setSuccessRate(starter.getSuccessRate());
        personalized.setIsActive(starter.getIsActive());
        personalized.setCreatedAt(starter.getCreatedAt());
        personalized.setUpdatedAt(starter.getUpdatedAt());
        
        return personalized;
    }
    
    @Transactional
    public void recordStarterUsage(Long starterId, boolean wasSuccessful) {
        ConversationStarter starter = conversationStarterRepository.findById(starterId)
            .orElseThrow(() -> new RuntimeException("Conversation starter not found"));
        
        starter.setUsageCount(starter.getUsageCount() + 1);
        
        // Update success rate
        double currentSuccessRate = starter.getSuccessRate();
        long totalUsage = starter.getUsageCount();
        
        if (wasSuccessful) {
            starter.setSuccessRate((currentSuccessRate * (totalUsage - 1) + 1.0) / totalUsage);
        } else {
            starter.setSuccessRate((currentSuccessRate * (totalUsage - 1)) / totalUsage);
        }
        
        conversationStarterRepository.save(starter);
    }
    
    @Transactional
    public void initializeDefaultStarters() {
        if (conversationStarterRepository.count() > 0) {
            return; // Already initialized
        }
        
        log.info("Initializing default conversation starters");
        
        List<ConversationStarter> defaultStarters = Arrays.asList(
            createStarter("What's the most interesting project you're working on right now?", 
                "Professional", "work,project,interesting", ConversationStarter.ContextType.PROFESSIONAL, 1),
            
            createStarter("I noticed you have experience with {skill}. How did you get started with that?", 
                "Skills", "skill,experience,learning", ConversationStarter.ContextType.SKILL_BASED, 2),
            
            createStarter("What drew you to the {industry} industry?", 
                "Industry", "industry,career,motivation", ConversationStarter.ContextType.INDUSTRY_BASED, 1),
            
            createStarter("I see we both enjoy {interest}. What got you into that?", 
                "Interests", "hobby,interest,passion", ConversationStarter.ContextType.INTEREST_BASED, 1),
            
            createStarter("What's the best piece of career advice you've ever received?", 
                "Career", "advice,career,wisdom", ConversationStarter.ContextType.PROFESSIONAL, 2),
            
            createStarter("If you could have coffee with anyone in your field, who would it be and why?", 
                "Aspirational", "inspiration,role model,field", ConversationStarter.ContextType.PROFESSIONAL, 2),
            
            createStarter("What's a skill you're currently trying to develop?", 
                "Learning", "skill,development,learning", ConversationStarter.ContextType.SKILL_BASED, 1),
            
            createStarter("What's the most surprising thing about your job that people wouldn't expect?", 
                "Insights", "job,surprising,insights", ConversationStarter.ContextType.PROFESSIONAL, 2),
            
            createStarter("How do you like to spend your time outside of work?", 
                "Personal", "hobbies,personal,balance", ConversationStarter.ContextType.CASUAL, 1),
            
            createStarter("What's a book, podcast, or resource that's had a big impact on your thinking?", 
                "Learning", "book,podcast,learning,impact", ConversationStarter.ContextType.GENERAL, 2),
            
            createStarter("What trends in your industry are you most excited about?", 
                "Trends", "trends,industry,future,excited", ConversationStarter.ContextType.INDUSTRY_BASED, 2),
            
            createStarter("If you could solve one problem in your field, what would it be?", 
                "Problem Solving", "problem,solution,field,impact", ConversationStarter.ContextType.PROFESSIONAL, 3),
            
            createStarter("What's something you've learned recently that changed your perspective?", 
                "Growth", "learning,perspective,growth,insight", ConversationStarter.ContextType.GENERAL, 2),
            
            createStarter("How do you stay motivated when working on challenging projects?", 
                "Motivation", "motivation,challenge,persistence", ConversationStarter.ContextType.PROFESSIONAL, 2),
            
            createStarter("What's your favorite way to learn new things?", 
                "Learning Style", "learning,education,growth", ConversationStarter.ContextType.GENERAL, 1)
        );
        
        conversationStarterRepository.saveAll(defaultStarters);
        log.info("Initialized {} default conversation starters", defaultStarters.size());
    }
    
    private ConversationStarter createStarter(String template, String category, String tags, 
                                            ConversationStarter.ContextType contextType, int difficulty) {
        ConversationStarter starter = new ConversationStarter();
        starter.setTemplate(template);
        starter.setCategory(category);
        starter.setTags(tags);
        starter.setContextType(contextType);
        starter.setDifficultyLevel(difficulty);
        starter.setUsageCount(0L);
        starter.setSuccessRate(0.5); // Start with neutral success rate
        starter.setIsActive(true);
        return starter;
    }
}
