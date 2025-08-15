package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.*;
import com.codewithudo.backend.entity.*;
import com.codewithudo.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoungeService {
    
    @Autowired
    private LoungeRepository loungeRepository;
    
    @Autowired
    private LoungeMessageRepository loungeMessageRepository;
    
    @Autowired
    private LoungeParticipantRepository loungeParticipantRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GamificationService gamificationService;
    
    public LoungeDto createLounge(Long userId, CreateLoungeDto createDto) {
        // Check if lounge title already exists
        if (loungeRepository.existsByTitleAndIsActiveTrue(createDto.getTitle())) {
            throw new RuntimeException("Lounge with this title already exists");
        }
        
        // Create new lounge
        Lounge lounge = new Lounge();
        lounge.setTitle(createDto.getTitle());
        lounge.setDescription(createDto.getDescription());
        lounge.setTopic(createDto.getTopic());
        lounge.setCategory(createDto.getCategory());
        lounge.setTags(createDto.getTags() != null ? String.join(",", createDto.getTags()) : null);
        lounge.setCreatedBy(userId);
        lounge.setVisibility(createDto.getVisibility());
        lounge.setMaxParticipants(createDto.getMaxParticipants());
        lounge.setCurrentParticipants(1); // Creator is first participant
        lounge.setLastActivity(LocalDateTime.now());
        
        Lounge savedLounge = loungeRepository.save(lounge);
        
        // Add creator as participant
        LoungeParticipant participant = new LoungeParticipant();
        participant.setLoungeId(savedLounge.getId());
        participant.setUserId(userId);
        participant.setRole(LoungeParticipant.ParticipantRole.CREATOR);
        participant.setJoinedAt(LocalDateTime.now());
        participant.setLastActivity(LocalDateTime.now());
        
        loungeParticipantRepository.save(participant);
        
        // Log system message
        logSystemMessage(savedLounge.getId(), userId, "Lounge created", LoungeMessage.MessageType.SYSTEM);
        
        // Log activity for gamification
        gamificationService.logActivity(userId, ActivityLog.ActivityType.LOUNGE_CREATED, savedLounge.getId(), null);
        
        return convertToLoungeDto(savedLounge, userId);
    }
    
    public LoungeDto getLoungeById(Long loungeId, Long userId) {
        Lounge lounge = loungeRepository.findByIdAndIsActiveTrue(loungeId)
                .orElseThrow(() -> new RuntimeException("Lounge not found"));
        
        return convertToLoungeDto(lounge, userId);
    }
    
    public List<LoungeDto> getAllLounges(Long userId) {
        List<Lounge> lounges = loungeRepository.findByIsActiveTrueOrderByLastActivityDesc();
        return lounges.stream()
                .map(lounge -> convertToLoungeDto(lounge, userId))
                .collect(Collectors.toList());
    }
    
    public List<LoungeDto> searchLounges(String searchTerm, Long userId) {
        List<Lounge> lounges = loungeRepository.searchLounges(searchTerm);
        return lounges.stream()
                .map(lounge -> convertToLoungeDto(lounge, userId))
                .collect(Collectors.toList());
    }
    
    public List<LoungeDto> getLoungesByTopic(String topic, Long userId) {
        List<Lounge> lounges = loungeRepository.findByTopicContainingIgnoreCaseAndIsActiveTrue(topic);
        return lounges.stream()
                .map(lounge -> convertToLoungeDto(lounge, userId))
                .collect(Collectors.toList());
    }
    
    public List<LoungeDto> getLoungesByCategory(String category, Long userId) {
        List<Lounge> lounges = loungeRepository.findByCategoryContainingIgnoreCaseAndIsActiveTrue(category);
        return lounges.stream()
                .map(lounge -> convertToLoungeDto(lounge, userId))
                .collect(Collectors.toList());
    }
    
    public List<LoungeDto> getFeaturedLounges(Long userId) {
        List<Lounge> lounges = loungeRepository.findByIsFeaturedTrueAndIsActiveTrue();
        return lounges.stream()
                .map(lounge -> convertToLoungeDto(lounge, userId))
                .collect(Collectors.toList());
    }
    
    public List<LoungeDto> getUserLounges(Long userId) {
        List<Lounge> lounges = loungeRepository.findByCreatedByAndIsActiveTrue(userId);
        return lounges.stream()
                .map(lounge -> convertToLoungeDto(lounge, userId))
                .collect(Collectors.toList());
    }
    
    public LoungeDto joinLounge(Long loungeId, Long userId) {
        Lounge lounge = loungeRepository.findByIdAndIsActiveTrue(loungeId)
                .orElseThrow(() -> new RuntimeException("Lounge not found"));
        
        // Check if user is already a participant
        if (loungeParticipantRepository.existsByLoungeIdAndUserIdAndIsActiveTrue(loungeId, userId)) {
            throw new RuntimeException("User is already a participant in this lounge");
        }
        
        // Check if lounge is full
        if (lounge.getMaxParticipants() != null && 
            lounge.getCurrentParticipants() >= lounge.getMaxParticipants()) {
            throw new RuntimeException("Lounge is at maximum capacity");
        }
        
        // Add user as participant
        LoungeParticipant participant = new LoungeParticipant();
        participant.setLoungeId(loungeId);
        participant.setUserId(userId);
        participant.setRole(LoungeParticipant.ParticipantRole.MEMBER);
        participant.setJoinedAt(LocalDateTime.now());
        participant.setLastActivity(LocalDateTime.now());
        
        loungeParticipantRepository.save(participant);
        
        // Update lounge participant count
        lounge.setCurrentParticipants(lounge.getCurrentParticipants() + 1);
        lounge.setLastActivity(LocalDateTime.now());
        loungeRepository.save(lounge);
        
        // Log join message
        logSystemMessage(loungeId, userId, "joined the lounge", LoungeMessage.MessageType.JOIN);
        
        // Log activity for gamification
        gamificationService.logActivity(userId, ActivityLog.ActivityType.LOUNGE_JOINED, loungeId, null);
        
        return convertToLoungeDto(lounge, userId);
    }
    
    public void leaveLounge(Long loungeId, Long userId) {
        LoungeParticipant participant = loungeParticipantRepository.findByLoungeIdAndUserIdAndIsActiveTrue(loungeId, userId)
                .orElseThrow(() -> new RuntimeException("User is not a participant in this lounge"));
        
        // Check if user is the creator
        if (participant.getRole() == LoungeParticipant.ParticipantRole.CREATOR) {
            throw new RuntimeException("Creator cannot leave the lounge. Transfer ownership or delete the lounge instead.");
        }
        
        // Remove participant
        participant.setIsActive(false);
        loungeParticipantRepository.save(participant);
        
        // Update lounge participant count
        Lounge lounge = loungeRepository.findByIdAndIsActiveTrue(loungeId)
                .orElseThrow(() -> new RuntimeException("Lounge not found"));
        lounge.setCurrentParticipants(Math.max(0, lounge.getCurrentParticipants() - 1));
        lounge.setLastActivity(LocalDateTime.now());
        loungeRepository.save(lounge);
        
        // Log leave message
        logSystemMessage(loungeId, userId, "left the lounge", LoungeMessage.MessageType.LEAVE);
    }
    
    public List<LoungeMessageDto> getLoungeMessages(Long loungeId, Long userId) {
        // Verify user is a participant
        if (!loungeParticipantRepository.existsByLoungeIdAndUserIdAndIsActiveTrue(loungeId, userId)) {
            throw new RuntimeException("User is not a participant in this lounge");
        }
        
        List<LoungeMessage> messages = loungeMessageRepository.findByLoungeIdAndIsDeletedFalseOrderByCreatedAtAsc(loungeId);
        return messages.stream()
                .map(this::convertToMessageDto)
                .collect(Collectors.toList());
    }
    
    public LoungeMessageDto sendMessage(Long userId, SendMessageDto sendDto) {
        // Verify user is a participant
        if (!loungeParticipantRepository.existsByLoungeIdAndUserIdAndIsActiveTrue(sendDto.getLoungeId(), userId)) {
            throw new RuntimeException("User is not a participant in this lounge");
        }
        
        // Check if user is muted
        LoungeParticipant participant = loungeParticipantRepository.findByLoungeIdAndUserIdAndIsActiveTrue(sendDto.getLoungeId(), userId)
                .orElseThrow(() -> new RuntimeException("User is not a participant in this lounge"));
        
        if (participant.getIsMuted() && participant.getMutedUntil() != null && 
            participant.getMutedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("User is currently muted");
        }
        
        // Create message
        LoungeMessage message = new LoungeMessage();
        message.setLoungeId(sendDto.getLoungeId());
        message.setUserId(userId);
        message.setContent(sendDto.getContent());
        message.setMessageType(sendDto.getMessageType());
        message.setReplyToMessageId(sendDto.getReplyToMessageId());
        
        LoungeMessage savedMessage = loungeMessageRepository.save(message);
        
        // Update lounge last activity
        Lounge lounge = loungeRepository.findByIdAndIsActiveTrue(sendDto.getLoungeId())
                .orElseThrow(() -> new RuntimeException("Lounge not found"));
        lounge.setLastActivity(LocalDateTime.now());
        loungeRepository.save(lounge);
        
        // Update participant last activity
        participant.setLastActivity(LocalDateTime.now());
        loungeParticipantRepository.save(participant);
        
        // Log activity for gamification
        gamificationService.logActivity(userId, ActivityLog.ActivityType.LOUNGE_MESSAGE_SENT, sendDto.getLoungeId(), null);
        
        return convertToMessageDto(savedMessage);
    }
    
    public void deleteLounge(Long loungeId, Long userId) {
        Lounge lounge = loungeRepository.findByIdAndIsActiveTrue(loungeId)
                .orElseThrow(() -> new RuntimeException("Lounge not found"));
        
        // Check if user is the creator
        if (!lounge.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Only the creator can delete the lounge");
        }
        
        // Soft delete the lounge
        lounge.setIsActive(false);
        loungeRepository.save(lounge);
        
        // Deactivate all participants
        List<LoungeParticipant> participants = loungeParticipantRepository.findByLoungeIdAndIsActiveTrue(loungeId);
        participants.forEach(participant -> {
            participant.setIsActive(false);
            loungeParticipantRepository.save(participant);
        });
    }
    
    private void logSystemMessage(Long loungeId, Long userId, String content, LoungeMessage.MessageType messageType) {
        LoungeMessage message = new LoungeMessage();
        message.setLoungeId(loungeId);
        message.setUserId(userId);
        message.setContent(content);
        message.setMessageType(messageType);
        
        loungeMessageRepository.save(message);
    }
    
    private LoungeDto convertToLoungeDto(Lounge lounge, Long userId) {
        LoungeDto dto = new LoungeDto();
        dto.setId(lounge.getId());
        dto.setTitle(lounge.getTitle());
        dto.setDescription(lounge.getDescription());
        dto.setTopic(lounge.getTopic());
        dto.setCategory(lounge.getCategory());
        dto.setTags(lounge.getTags());
        dto.setCreatedBy(lounge.getCreatedBy());
        dto.setVisibility(lounge.getVisibility());
        dto.setMaxParticipants(lounge.getMaxParticipants());
        dto.setCurrentParticipants(lounge.getCurrentParticipants());
        dto.setIsActive(lounge.getIsActive());
        dto.setIsFeatured(lounge.getIsFeatured());
        dto.setLastActivity(lounge.getLastActivity());
        dto.setCreatedAt(lounge.getCreatedAt());
        dto.setUpdatedAt(lounge.getUpdatedAt());
        
        // Set tag list
        if (lounge.getTags() != null) {
            dto.setTagList(Arrays.asList(lounge.getTags().split(",")));
        }
        
        // Set creator info
        userRepository.findById(lounge.getCreatedBy()).ifPresent(creator -> {
            dto.setCreatorName(creator.getName());
            dto.setCreatorEmail(creator.getEmail());
        });
        
        // Check if user is participant
        Optional<LoungeParticipant> participant = loungeParticipantRepository.findByLoungeIdAndUserIdAndIsActiveTrue(lounge.getId(), userId);
        dto.setIsParticipant(participant.isPresent());
        participant.ifPresent(p -> dto.setParticipantRole(p.getRole().toString()));
        
        return dto;
    }
    
    private LoungeMessageDto convertToMessageDto(LoungeMessage message) {
        LoungeMessageDto dto = new LoungeMessageDto();
        dto.setId(message.getId());
        dto.setLoungeId(message.getLoungeId());
        dto.setUserId(message.getUserId());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setReplyToMessageId(message.getReplyToMessageId());
        dto.setIsEdited(message.getIsEdited());
        dto.setEditedAt(message.getEditedAt());
        dto.setIsDeleted(message.getIsDeleted());
        dto.setCreatedAt(message.getCreatedAt());
        
        // Set user info
        userRepository.findById(message.getUserId()).ifPresent(user -> {
            dto.setUserName(user.getName());
            dto.setUserEmail(user.getEmail());
        });
        
        // Set reply to user info
        if (message.getReplyToMessageId() != null) {
            loungeMessageRepository.findById(message.getReplyToMessageId()).ifPresent(replyTo -> {
                userRepository.findById(replyTo.getUserId()).ifPresent(user -> {
                    dto.setReplyToUserName(user.getName());
                });
            });
        }
        
        return dto;
    }
}
