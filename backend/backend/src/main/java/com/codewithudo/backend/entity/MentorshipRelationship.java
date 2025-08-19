package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mentorship_relationships")
public class MentorshipRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private MentorshipProgram program;

    @Column(name = "mentor_id", nullable = false)
    private Long mentorId;

    @Column(name = "mentee_id", nullable = false)
    private Long menteeId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "goals", columnDefinition = "TEXT")
    private String goals;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "mentor_rating")
    private Integer mentorRating;

    @Column(name = "mentee_rating")
    private Integer menteeRating;

    @Column(name = "mentor_feedback", columnDefinition = "TEXT")
    private String mentorFeedback;

    @Column(name = "mentee_feedback", columnDefinition = "TEXT")
    private String menteeFeedback;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "relationship", cascade = CascadeType.ALL)
    private List<MentorshipSession> sessions;

    public enum Status {
        PENDING,
        ACTIVE,
        COMPLETED,
        TERMINATED
    }

    // Constructors
    public MentorshipRelationship() {}

    public MentorshipRelationship(MentorshipProgram program, Long mentorId, Long menteeId, String goals) {
        this.program = program;
        this.mentorId = mentorId;
        this.menteeId = menteeId;
        this.goals = goals;
        this.status = Status.PENDING;
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

    public MentorshipProgram getProgram() {
        return program;
    }

    public void setProgram(MentorshipProgram program) {
        this.program = program;
    }

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public Long getMenteeId() {
        return menteeId;
    }

    public void setMenteeId(Long menteeId) {
        this.menteeId = menteeId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getMentorRating() {
        return mentorRating;
    }

    public void setMentorRating(Integer mentorRating) {
        this.mentorRating = mentorRating;
    }

    public Integer getMenteeRating() {
        return menteeRating;
    }

    public void setMenteeRating(Integer menteeRating) {
        this.menteeRating = menteeRating;
    }

    public String getMentorFeedback() {
        return mentorFeedback;
    }

    public void setMentorFeedback(String mentorFeedback) {
        this.mentorFeedback = mentorFeedback;
    }

    public String getMenteeFeedback() {
        return menteeFeedback;
    }

    public void setMenteeFeedback(String menteeFeedback) {
        this.menteeFeedback = menteeFeedback;
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

    public List<MentorshipSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<MentorshipSession> sessions) {
        this.sessions = sessions;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
