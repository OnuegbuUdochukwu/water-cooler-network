package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mentorship_sessions")
public class MentorshipSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_id", nullable = false)
    private MentorshipRelationship relationship;

    @Column(name = "session_date", nullable = false)
    private LocalDateTime sessionDate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "session_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionType sessionType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "agenda", columnDefinition = "TEXT")
    private String agenda;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "action_items", columnDefinition = "TEXT")
    private String actionItems;

    @Column(name = "mentor_notes", columnDefinition = "TEXT")
    private String mentorNotes;

    @Column(name = "mentee_notes", columnDefinition = "TEXT")
    private String menteeNotes;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum SessionType {
        ONE_ON_ONE,
        GROUP_SESSION,
        WORKSHOP,
        PRESENTATION,
        CODE_REVIEW,
        CAREER_GUIDANCE,
        SKILL_ASSESSMENT,
        NETWORKING
    }

    public enum Status {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }

    // Constructors
    public MentorshipSession() {}

    public MentorshipSession(MentorshipRelationship relationship, LocalDateTime sessionDate,
                           SessionType sessionType, String title, String description) {
        this.relationship = relationship;
        this.sessionDate = sessionDate;
        this.sessionType = sessionType;
        this.title = title;
        this.description = description;
        this.status = Status.SCHEDULED;
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

    public MentorshipRelationship getRelationship() {
        return relationship;
    }

    public void setRelationship(MentorshipRelationship relationship) {
        this.relationship = relationship;
    }

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getActionItems() {
        return actionItems;
    }

    public void setActionItems(String actionItems) {
        this.actionItems = actionItems;
    }

    public String getMentorNotes() {
        return mentorNotes;
    }

    public void setMentorNotes(String mentorNotes) {
        this.mentorNotes = mentorNotes;
    }

    public String getMenteeNotes() {
        return menteeNotes;
    }

    public void setMenteeNotes(String menteeNotes) {
        this.menteeNotes = menteeNotes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
