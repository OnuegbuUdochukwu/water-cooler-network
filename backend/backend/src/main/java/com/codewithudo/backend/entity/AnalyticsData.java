package com.codewithudo.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_data")
public class AnalyticsData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "metric_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MetricType metricType;

    @Column(name = "metric_value")
    private Double metricValue;

    @Column(name = "metric_count")
    private Integer metricCount;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "period_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PeriodType periodType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum MetricType {
        DAILY_ACTIVE_USERS,
        WEEKLY_ACTIVE_USERS,
        MONTHLY_ACTIVE_USERS,
        TOTAL_CONVERSATIONS,
        TOTAL_VIDEO_CALLS,
        AVERAGE_SESSION_DURATION,
        BADGES_EARNED,
        STREAKS_MAINTAINED,
        EMPLOYEE_SATISFACTION,
        RESPONSE_RATE,
        MENTORSHIP_MATCHES,
        TOPIC_ENGAGEMENT
    }

    public enum PeriodType {
        DAILY,
        WEEKLY,
        MONTHLY,
        QUARTERLY,
        YEARLY
    }

    // Constructors
    public AnalyticsData() {}

    public AnalyticsData(Long companyId, Long departmentId, MetricType metricType, 
                        Double metricValue, Integer metricCount, LocalDate date, 
                        PeriodType periodType) {
        this.companyId = companyId;
        this.departmentId = departmentId;
        this.metricType = metricType;
        this.metricValue = metricValue;
        this.metricCount = metricCount;
        this.date = date;
        this.periodType = periodType;
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

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    public Integer getMetricCount() {
        return metricCount;
    }

    public void setMetricCount(Integer metricCount) {
        this.metricCount = metricCount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
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
