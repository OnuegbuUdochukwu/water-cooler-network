package com.codewithudo.backend.dto;

import com.codewithudo.backend.entity.MentorshipProgram;
import java.time.LocalDateTime;

public class MentorshipProgramDTO {

    private Long id;
    private Long companyId;
    private String programName;
    private String description;
    private String programType;
    private Integer durationWeeks;
    private Integer maxMenteesPerMentor;
    private Integer minMentorExperienceYears;
    private Boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer currentParticipants;
    private Integer maxParticipants;

    // Constructors
    public MentorshipProgramDTO() {}

    public MentorshipProgramDTO(MentorshipProgram program) {
        this.id = program.getId();
        this.companyId = program.getCompanyId();
        this.programName = program.getProgramName();
        this.description = program.getDescription();
        this.programType = program.getProgramType().name();
        this.durationWeeks = program.getDurationWeeks();
        this.maxMenteesPerMentor = program.getMaxMenteesPerMentor();
        this.minMentorExperienceYears = program.getMinMentorExperienceYears();
        this.isActive = program.getIsActive();
        this.startDate = program.getStartDate();
        this.endDate = program.getEndDate();
        this.createdAt = program.getCreatedAt();
        this.updatedAt = program.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public Integer getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(Integer durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public Integer getMaxMenteesPerMentor() {
        return maxMenteesPerMentor;
    }

    public void setMaxMenteesPerMentor(Integer maxMenteesPerMentor) {
        this.maxMenteesPerMentor = maxMenteesPerMentor;
    }

    public Integer getMinMentorExperienceYears() {
        return minMentorExperienceYears;
    }

    public void setMinMentorExperienceYears(Integer minMentorExperienceYears) {
        this.minMentorExperienceYears = minMentorExperienceYears;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    // Helper methods
    public String getProgramTypeDisplay() {
        return programType.replace("_", " ").toLowerCase();
    }

    public Boolean getIsFull() {
        return currentParticipants != null && maxParticipants != null && 
               currentParticipants >= maxParticipants;
    }

    public String getStatusDisplay() {
        if (!isActive) return "Inactive";
        if (getIsFull()) return "Full";
        if (startDate != null && LocalDateTime.now().isBefore(startDate)) return "Upcoming";
        if (endDate != null && LocalDateTime.now().isAfter(endDate)) return "Completed";
        return "Active";
    }
}
