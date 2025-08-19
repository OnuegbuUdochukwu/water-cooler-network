package com.codewithudo.backend.service;

import com.codewithudo.backend.dto.AnalyticsResponseDTO;
import com.codewithudo.backend.entity.AnalyticsData;
import com.codewithudo.backend.repository.AnalyticsDataRepository;
import com.codewithudo.backend.repository.UserRepository;
import com.codewithudo.backend.repository.ActivityLogRepository;
import com.codewithudo.backend.repository.UserBadgeRepository;
import com.codewithudo.backend.repository.UserStreakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    
    @Autowired
    private AnalyticsDataRepository analyticsDataRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private UserBadgeRepository userBadgeRepository;

    @Autowired
    private UserStreakRepository userStreakRepository;

    @Cacheable(value = "analytics", key = "#companyId + '_' + #metricType + '_' + #startDate + '_' + #endDate + '_' + #periodType")
    public AnalyticsResponseDTO getAnalytics(Long companyId, Long departmentId, 
                                          AnalyticsData.MetricType metricType,
                                          LocalDate startDate, LocalDate endDate,
                                          AnalyticsData.PeriodType periodType) {
        
        List<AnalyticsData> data = departmentId != null ?
                analyticsDataRepository.findByCompanyAndDepartmentAndDateRange(
                        companyId, departmentId, startDate, endDate, periodType) :
                analyticsDataRepository.findByCompanyAndMetricTypeAndDateRange(
                        companyId, metricType, startDate, endDate, periodType);

        if (data.isEmpty()) {
            // Generate analytics data if none exists
            data = generateAnalyticsData(companyId, departmentId, metricType, startDate, endDate, periodType);
        }

        return buildAnalyticsResponse(metricType, periodType, startDate, endDate, data);
    }

    @Cacheable(value = "analytics", key = "#companyId + '_overview'")
    public Map<String, Object> getCompanyOverview(Long companyId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        Map<String, Object> overview = new HashMap<>();
        
        // Active users
        overview.put("dailyActiveUsers", getDailyActiveUsers(companyId, endDate, null));
        overview.put("weeklyActiveUsers", getWeeklyActiveUsers(companyId, endDate, null));
        overview.put("monthlyActiveUsers", getMonthlyActiveUsers(companyId, endDate, null));
        
        // Engagement metrics
        overview.put("totalConversations", getTotalConversations(companyId, startDate, endDate, null));
        overview.put("totalVideoCalls", getTotalVideoCalls(companyId, startDate, endDate, null));
        overview.put("averageSessionDuration", getAverageSessionDuration(companyId, startDate, endDate, null));
        
        // Gamification metrics
        overview.put("badgesEarned", getBadgesEarned(companyId, startDate, endDate, null));
        overview.put("streaksMaintained", getStreaksMaintained(companyId, startDate, endDate, null));
        
        // Growth metrics
        overview.put("userGrowth", calculateUserGrowth(companyId, startDate, endDate));
        overview.put("engagementGrowth", calculateEngagementGrowth(companyId, startDate, endDate));
        
        return overview;
    }
    
    @Cacheable(value = "analytics", key = "#companyId + '_department_' + #departmentId")
    public Map<String, Object> getDepartmentAnalytics(Long companyId, Long departmentId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("activeUsers", getDailyActiveUsers(companyId, endDate, departmentId));
        analytics.put("engagement", getDepartmentEngagement(companyId, departmentId, startDate, endDate));
        analytics.put("performance", getDepartmentPerformance(companyId, departmentId, startDate, endDate));
        analytics.put("comparison", compareDepartmentToCompany(companyId, departmentId, startDate, endDate));

        return analytics;
    }

    public void generateDailyAnalytics(Long companyId) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // Generate analytics for all metrics
        for (AnalyticsData.MetricType metricType : AnalyticsData.MetricType.values()) {
            generateMetricAnalytics(companyId, null, metricType, yesterday, AnalyticsData.PeriodType.DAILY);
        }
    }

    public void generateWeeklyAnalytics(Long companyId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);

        for (AnalyticsData.MetricType metricType : AnalyticsData.MetricType.values()) {
            generateMetricAnalytics(companyId, null, metricType, startDate, AnalyticsData.PeriodType.WEEKLY);
        }
    }

    public void generateMonthlyAnalytics(Long companyId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        for (AnalyticsData.MetricType metricType : AnalyticsData.MetricType.values()) {
            generateMetricAnalytics(companyId, null, metricType, startDate, AnalyticsData.PeriodType.MONTHLY);
        }
    }

    private List<AnalyticsData> generateAnalyticsData(Long companyId, Long departmentId,
                                                    AnalyticsData.MetricType metricType,
                                                    LocalDate startDate, LocalDate endDate,
                                                    AnalyticsData.PeriodType periodType) {
        List<AnalyticsData> data = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            AnalyticsData analyticsData = generateMetricAnalytics(companyId, departmentId, metricType, currentDate, periodType);
            if (analyticsData != null) {
                data.add(analyticsData);
            }
            currentDate = currentDate.plusDays(1);
        }

        return data;
    }

    private AnalyticsData generateMetricAnalytics(Long companyId, Long departmentId,
                                               AnalyticsData.MetricType metricType,
                                               LocalDate date, AnalyticsData.PeriodType periodType) {
        
        Double metricValue = 0.0;
        Integer metricCount = 0;

        switch (metricType) {
            case DAILY_ACTIVE_USERS:
                metricValue = (double) getDailyActiveUsers(companyId, date, departmentId);
                metricCount = getDailyActiveUsers(companyId, date, departmentId);
                break;
            case WEEKLY_ACTIVE_USERS:
                metricValue = (double) getWeeklyActiveUsers(companyId, date, departmentId);
                metricCount = getWeeklyActiveUsers(companyId, date, departmentId);
                break;
            case MONTHLY_ACTIVE_USERS:
                metricValue = (double) getMonthlyActiveUsers(companyId, date, departmentId);
                metricCount = getMonthlyActiveUsers(companyId, date, departmentId);
                break;
            case TOTAL_CONVERSATIONS:
                metricValue = (double) getTotalConversations(companyId, date, date, departmentId);
                metricCount = getTotalConversations(companyId, date, date, departmentId);
                break;
            case TOTAL_VIDEO_CALLS:
                metricValue = (double) getTotalVideoCalls(companyId, date, date, departmentId);
                metricCount = getTotalVideoCalls(companyId, date, date, departmentId);
                break;
            case AVERAGE_SESSION_DURATION:
                metricValue = getAverageSessionDuration(companyId, date, date, departmentId);
                metricCount = 1;
                break;
            case BADGES_EARNED:
                metricValue = (double) getBadgesEarned(companyId, date, date, departmentId);
                metricCount = getBadgesEarned(companyId, date, date, departmentId);
                break;
            case STREAKS_MAINTAINED:
                metricValue = (double) getStreaksMaintained(companyId, date, date, departmentId);
                metricCount = getStreaksMaintained(companyId, date, date, departmentId);
                break;
            default:
                return null;
        }

        AnalyticsData analyticsData = new AnalyticsData(companyId, departmentId, metricType, 
                                                      metricValue, metricCount, date, periodType);
        return analyticsDataRepository.save(analyticsData);
    }

    private AnalyticsResponseDTO buildAnalyticsResponse(AnalyticsData.MetricType metricType,
                                                      AnalyticsData.PeriodType periodType,
                                                      LocalDate startDate, LocalDate endDate,
                                                      List<AnalyticsData> data) {
        
        List<AnalyticsResponseDTO.DataPoint> dataPoints = data.stream()
                .map(d -> new AnalyticsResponseDTO.DataPoint(d.getDate(), d.getMetricValue(), 
                                                           d.getMetricCount(), d.getDate().toString()))
            .collect(Collectors.toList());

        AnalyticsResponseDTO.Summary summary = calculateSummary(data);
        
        return new AnalyticsResponseDTO(metricType.name(), periodType.name(), 
                                      startDate, endDate, dataPoints, summary);
    }

    private AnalyticsResponseDTO.Summary calculateSummary(List<AnalyticsData> data) {
        if (data.isEmpty()) {
            return new AnalyticsResponseDTO.Summary(0.0, 0, 0.0, 0.0, 0.0, 0.0, "stable");
        }

        Double totalValue = data.stream().mapToDouble(AnalyticsData::getMetricValue).sum();
        Integer totalCount = data.stream().mapToInt(AnalyticsData::getMetricCount).sum();
        Double averageValue = totalValue / data.size();
        Double minValue = data.stream().mapToDouble(AnalyticsData::getMetricValue).min().orElse(0.0);
        Double maxValue = data.stream().mapToDouble(AnalyticsData::getMetricValue).max().orElse(0.0);
        
        Double growthRate = calculateGrowthRate(data);
        String trend = determineTrend(growthRate);

        return new AnalyticsResponseDTO.Summary(totalValue, totalCount, averageValue, 
                                             minValue, maxValue, growthRate, trend);
    }

    private Double calculateGrowthRate(List<AnalyticsData> data) {
        if (data.size() < 2) return 0.0;
        
        AnalyticsData first = data.get(0);
        AnalyticsData last = data.get(data.size() - 1);
        
        if (first.getMetricValue() == 0) return 0.0;
        
        return ((last.getMetricValue() - first.getMetricValue()) / first.getMetricValue()) * 100;
    }

    private String determineTrend(Double growthRate) {
        if (growthRate > 5) return "increasing";
        if (growthRate < -5) return "decreasing";
        return "stable";
    }

    // Helper methods for specific metrics
    private Integer getDailyActiveUsers(Long companyId, LocalDate date, Long departmentId) {
        // Implementation would query user activity for the specific date
        return userRepository.countByCompanyIdAndLastActiveDateBetween(
                companyId, date.atStartOfDay(), date.atTime(23, 59, 59)).intValue();
    }

    private Integer getWeeklyActiveUsers(Long companyId, LocalDate date, Long departmentId) {
        LocalDate weekStart = date.minusDays(6);
        return userRepository.countByCompanyIdAndLastActiveDateBetween(
                companyId, weekStart.atStartOfDay(), date.atTime(23, 59, 59)).intValue();
    }

    private Integer getMonthlyActiveUsers(Long companyId, LocalDate date, Long departmentId) {
        LocalDate monthStart = date.minusDays(29);
        return userRepository.countByCompanyIdAndLastActiveDateBetween(
                companyId, monthStart.atStartOfDay(), date.atTime(23, 59, 59)).intValue();
    }

    private Integer getTotalConversations(Long companyId, LocalDate startDate, LocalDate endDate, Long departmentId) {
        // Implementation would query conversation data
        return 0; // Placeholder
    }

    private Integer getTotalVideoCalls(Long companyId, LocalDate startDate, LocalDate endDate, Long departmentId) {
        // Implementation would query video call data
        return 0; // Placeholder
    }

    private Double getAverageSessionDuration(Long companyId, LocalDate startDate, LocalDate endDate, Long departmentId) {
        // Implementation would query session duration data
        return 0.0; // Placeholder
    }

    private Integer getBadgesEarned(Long companyId, LocalDate startDate, LocalDate endDate, Long departmentId) {
        // Implementation would query badge data
        return 0; // Placeholder
    }

    private Integer getStreaksMaintained(Long companyId, LocalDate startDate, LocalDate endDate, Long departmentId) {
        // Implementation would query streak data
        return 0; // Placeholder
    }

    private Map<String, Object> getDepartmentEngagement(Long companyId, Long departmentId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> engagement = new HashMap<>();
        // Implementation would calculate department-specific engagement metrics
        return engagement;
    }

    private Map<String, Object> getDepartmentPerformance(Long companyId, Long departmentId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> performance = new HashMap<>();
        // Implementation would calculate department-specific performance metrics
        return performance;
    }

    private Map<String, Object> compareDepartmentToCompany(Long companyId, Long departmentId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> comparison = new HashMap<>();
        // Implementation would compare department metrics to company averages
        return comparison;
    }

    private Double calculateUserGrowth(Long companyId, LocalDate startDate, LocalDate endDate) {
        // Implementation would calculate user growth rate
        return 0.0; // Placeholder
    }

    private Double calculateEngagementGrowth(Long companyId, LocalDate startDate, LocalDate endDate) {
        // Implementation would calculate engagement growth rate
        return 0.0; // Placeholder
    }
}
