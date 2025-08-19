package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.MentorshipProgramDTO;
import com.codewithudo.backend.entity.MentorshipProgram;
import com.codewithudo.backend.entity.MentorshipRelationship;
import com.codewithudo.backend.entity.MentorshipSession;
import com.codewithudo.backend.repository.MentorshipProgramRepository;
import com.codewithudo.backend.repository.MentorshipRelationshipRepository;
import com.codewithudo.backend.repository.MentorshipSessionRepository;
import com.codewithudo.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MentorshipService {

    @Autowired
    private MentorshipProgramRepository programRepository;

    @Autowired
    private MentorshipRelationshipRepository relationshipRepository;

    @Autowired
    private MentorshipSessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    // Program Management
    public List<MentorshipProgramDTO> getActivePrograms(Long companyId) {
        List<MentorshipProgram> programs = programRepository.findByCompanyIdAndIsActiveTrue(companyId);
        return programs.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MentorshipProgramDTO getProgramById(Long programId) {
        Optional<MentorshipProgram> program = programRepository.findById(programId);
        return program.map(this::convertToDTO).orElse(null);
    }

    public MentorshipProgramDTO createProgram(MentorshipProgramDTO programDTO) {
        MentorshipProgram program = new MentorshipProgram(
                programDTO.getCompanyId(),
                programDTO.getProgramName(),
                programDTO.getDescription(),
                MentorshipProgram.ProgramType.valueOf(programDTO.getProgramType()),
                programDTO.getDurationWeeks(),
                programDTO.getMaxMenteesPerMentor(),
                programDTO.getMinMentorExperienceYears()
        );
        
        if (programDTO.getStartDate() != null) {
            program.setStartDate(programDTO.getStartDate());
        }
        if (programDTO.getEndDate() != null) {
            program.setEndDate(programDTO.getEndDate());
        }

        MentorshipProgram savedProgram = programRepository.save(program);
        return convertToDTO(savedProgram);
    }

    public MentorshipProgramDTO updateProgram(Long programId, MentorshipProgramDTO programDTO) {
        Optional<MentorshipProgram> existingProgram = programRepository.findById(programId);
        if (existingProgram.isPresent()) {
            MentorshipProgram program = existingProgram.get();
            program.setProgramName(programDTO.getProgramName());
            program.setDescription(programDTO.getDescription());
            program.setProgramType(MentorshipProgram.ProgramType.valueOf(programDTO.getProgramType()));
            program.setDurationWeeks(programDTO.getDurationWeeks());
            program.setMaxMenteesPerMentor(programDTO.getMaxMenteesPerMentor());
            program.setMinMentorExperienceYears(programDTO.getMinMentorExperienceYears());
            program.setIsActive(programDTO.getIsActive());
            program.setStartDate(programDTO.getStartDate());
            program.setEndDate(programDTO.getEndDate());

            MentorshipProgram updatedProgram = programRepository.save(program);
            return convertToDTO(updatedProgram);
        }
        return null;
    }

    public Boolean deleteProgram(Long programId) {
        if (programRepository.existsById(programId)) {
            programRepository.deleteById(programId);
            return true;
        }
        return false;
    }

    // Relationship Management
    public List<MentorshipRelationship> getUserRelationships(Long userId) {
        return relationshipRepository.findByMentorIdOrMenteeId(userId, userId);
    }

    public List<MentorshipRelationship> getMentorRelationships(Long mentorId) {
        return relationshipRepository.findByMentorId(mentorId);
    }

    public List<MentorshipRelationship> getMenteeRelationships(Long menteeId) {
        return relationshipRepository.findByMenteeId(menteeId);
    }

    public MentorshipRelationship createRelationship(Long programId, Long mentorId, Long menteeId, String goals) {
        Optional<MentorshipProgram> program = programRepository.findById(programId);
        if (program.isPresent()) {
            // Check if mentor can take more mentees
            List<MentorshipRelationship> currentRelationships = relationshipRepository.findByMentorId(mentorId);
            if (currentRelationships.size() >= program.get().getMaxMenteesPerMentor()) {
                throw new RuntimeException("Mentor has reached maximum number of mentees");
            }

            MentorshipRelationship relationship = new MentorshipRelationship(program.get(), mentorId, menteeId, goals);
            relationship.setStartDate(LocalDateTime.now());
            return relationshipRepository.save(relationship);
        }
        return null;
    }

    public MentorshipRelationship updateRelationshipStatus(Long relationshipId, MentorshipRelationship.Status status) {
        Optional<MentorshipRelationship> relationship = relationshipRepository.findById(relationshipId);
        if (relationship.isPresent()) {
            MentorshipRelationship rel = relationship.get();
            rel.setStatus(status);
            if (status == MentorshipRelationship.Status.ACTIVE) {
                rel.setStartDate(LocalDateTime.now());
            } else if (status == MentorshipRelationship.Status.COMPLETED) {
                rel.setEndDate(LocalDateTime.now());
            }
            return relationshipRepository.save(rel);
        }
        return null;
    }

    public MentorshipRelationship addFeedback(Long relationshipId, Long userId, String feedback, Integer rating) {
        Optional<MentorshipRelationship> relationship = relationshipRepository.findById(relationshipId);
        if (relationship.isPresent()) {
            MentorshipRelationship rel = relationship.get();
            if (rel.getMentorId().equals(userId)) {
                rel.setMentorFeedback(feedback);
                rel.setMentorRating(rating);
            } else if (rel.getMenteeId().equals(userId)) {
                rel.setMenteeFeedback(feedback);
                rel.setMenteeRating(rating);
            }
            return relationshipRepository.save(rel);
        }
        return null;
    }

    // Session Management
    public List<MentorshipSession> getRelationshipSessions(Long relationshipId) {
        return sessionRepository.findByRelationshipIdOrderBySessionDateDesc(relationshipId);
    }

    public MentorshipSession createSession(Long relationshipId, MentorshipSessionDTO sessionDTO) {
        Optional<MentorshipRelationship> relationship = relationshipRepository.findById(relationshipId);
        if (relationship.isPresent()) {
            MentorshipSession session = new MentorshipSession(
                    relationship.get(),
                    sessionDTO.getSessionDate(),
                    MentorshipSession.SessionType.valueOf(sessionDTO.getSessionType()),
                    sessionDTO.getTitle(),
                    sessionDTO.getDescription()
            );
            session.setDurationMinutes(sessionDTO.getDurationMinutes());
            session.setAgenda(sessionDTO.getAgenda());
            return sessionRepository.save(session);
        }
        return null;
    }

    public MentorshipSession updateSession(Long sessionId, MentorshipSessionDTO sessionDTO) {
        Optional<MentorshipSession> session = sessionRepository.findById(sessionId);
        if (session.isPresent()) {
            MentorshipSession sess = session.get();
            sess.setSessionDate(sessionDTO.getSessionDate());
            sess.setSessionType(MentorshipSession.SessionType.valueOf(sessionDTO.getSessionType()));
            sess.setTitle(sessionDTO.getTitle());
            sess.setDescription(sessionDTO.getDescription());
            sess.setDurationMinutes(sessionDTO.getDurationMinutes());
            sess.setAgenda(sessionDTO.getAgenda());
            sess.setNotes(sessionDTO.getNotes());
            sess.setActionItems(sessionDTO.getActionItems());
            return sessionRepository.save(sess);
        }
        return null;
    }

    public MentorshipSession updateSessionStatus(Long sessionId, MentorshipSession.Status status) {
        Optional<MentorshipSession> session = sessionRepository.findById(sessionId);
        if (session.isPresent()) {
            MentorshipSession sess = session.get();
            sess.setStatus(status);
            return sessionRepository.save(sess);
        }
        return null;
    }

    public MentorshipSession addSessionNotes(Long sessionId, Long userId, String notes, String noteType) {
        Optional<MentorshipSession> session = sessionRepository.findById(sessionId);
        if (session.isPresent()) {
            MentorshipSession sess = session.get();
            if ("mentor".equals(noteType)) {
                sess.setMentorNotes(notes);
            } else if ("mentee".equals(noteType)) {
                sess.setMenteeNotes(notes);
            } else {
                sess.setNotes(notes);
            }
            return sessionRepository.save(sess);
        }
        return null;
    }

    // Matching Algorithm
    public List<Map<String, Object>> findMentorsForMentee(Long menteeId, Long programId) {
        Optional<MentorshipProgram> program = programRepository.findById(programId);
        if (program.isPresent()) {
            // Get potential mentors based on program requirements
            List<Map<String, Object>> potentialMentors = new ArrayList<>();
            
            // This is a simplified matching algorithm
            // In a real implementation, you would consider:
            // - Skills and experience
            // - Availability
            // - Current mentee load
            // - Compatibility scores
            
            return potentialMentors;
        }
        return new ArrayList<>();
    }

    public List<Map<String, Object>> findMenteesForMentor(Long mentorId, Long programId) {
        Optional<MentorshipProgram> program = programRepository.findById(programId);
        if (program.isPresent()) {
            // Get potential mentees based on program requirements
            List<Map<String, Object>> potentialMentees = new ArrayList<>();
            
            // This is a simplified matching algorithm
            // In a real implementation, you would consider:
            // - Learning goals
            // - Current skill level
            // - Availability
            // - Compatibility scores
            
            return potentialMentees;
        }
        return new ArrayList<>();
    }

    // Helper methods
    private MentorshipProgramDTO convertToDTO(MentorshipProgram program) {
        MentorshipProgramDTO dto = new MentorshipProgramDTO(program);
        
        // Calculate current participants
        List<MentorshipRelationship> relationships = relationshipRepository.findByProgram_Id(program.getId());
        dto.setCurrentParticipants(relationships.size());
        
        // Calculate max participants (simplified)
        if (program.getMaxMenteesPerMentor() != null) {
            dto.setMaxParticipants(program.getMaxMenteesPerMentor() * 10); // Assume 10 mentors max
        }
        
        return dto;
    }

    // DTO for session creation/updates
    public static class MentorshipSessionDTO {
        private LocalDateTime sessionDate;
        private String sessionType;
        private String title;
        private String description;
        private Integer durationMinutes;
        private String agenda;
        private String notes;
        private String actionItems;

        // Getters and Setters
        public LocalDateTime getSessionDate() { return sessionDate; }
        public void setSessionDate(LocalDateTime sessionDate) { this.sessionDate = sessionDate; }
        
        public String getSessionType() { return sessionType; }
        public void setSessionType(String sessionType) { this.sessionType = sessionType; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
        
        public String getAgenda() { return agenda; }
        public void setAgenda(String agenda) { this.agenda = agenda; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        public String getActionItems() { return actionItems; }
        public void setActionItems(String actionItems) { this.actionItems = actionItems; }
    }
}
