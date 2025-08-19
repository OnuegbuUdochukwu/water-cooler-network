package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mentorship_programs")
public class MentorshipProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "program_name", nullable = false)
    private String programName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "program_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProgramType programType;

    @Column(name = "duration_weeks")
    private Integer durationWeeks;

    @Column(name = "max_mentees_per_mentor")
    private Integer maxMenteesPerMentor;

    @Column(name = "min_mentor_experience_years")
    private Integer minMentorExperienceYears;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    private List<MentorshipRelationship> relationships;

    public enum ProgramType {
        TECHNICAL_SKILLS,
        LEADERSHIP_DEVELOPMENT,
        CAREER_GROWTH,
        SOFT_SKILLS,
        INDUSTRY_KNOWLEDGE,
        NETWORKING,
        GENERAL_MENTORSHIP
    }

    // Constructors
    public MentorshipProgram() {}

    public MentorshipProgram(Long companyId, String programName, String description,
                           ProgramType programType, Integer durationWeeks,
                           Integer maxMenteesPerMentor, Integer minMentorExperienceYears) {
        this.companyId = companyId;
        this.programName = programName;
        this.description = description;
        this.programType = programType;
        this.durationWeeks = durationWeeks;
        this.maxMenteesPerMentor = maxMenteesPerMentor;
        this.minMentorExperienceYears = minMentorExperienceYears;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public ProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(ProgramType programType) {
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

    public List<MentorshipRelationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<MentorshipRelationship> relationships) {
        this.relationships = relationships;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
