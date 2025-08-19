package com.codewithudo.backend.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AnalyticsResponseDTO {

    private String metricType;
    private String periodType;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DataPoint> dataPoints;
    private Summary summary;
    private Map<String, Object> additionalMetrics;

    public static class DataPoint {
        private LocalDate date;
        private Double value;
        private Integer count;
        private String label;

        public DataPoint() {}

        public DataPoint(LocalDate date, Double value, Integer count, String label) {
            this.date = date;
            this.value = value;
            this.count = count;
            this.label = label;
        }

        // Getters and Setters
        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    public static class Summary {
        private Double totalValue;
        private Integer totalCount;
        private Double averageValue;
        private Double minValue;
        private Double maxValue;
        private Double growthRate;
        private String trend;

        public Summary() {}

        public Summary(Double totalValue, Integer totalCount, Double averageValue, 
                      Double minValue, Double maxValue, Double growthRate, String trend) {
            this.totalValue = totalValue;
            this.totalCount = totalCount;
            this.averageValue = averageValue;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.growthRate = growthRate;
            this.trend = trend;
        }

        // Getters and Setters
        public Double getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(Double totalValue) {
            this.totalValue = totalValue;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Double getAverageValue() {
            return averageValue;
        }

        public void setAverageValue(Double averageValue) {
            this.averageValue = averageValue;
        }

        public Double getMinValue() {
            return minValue;
        }

        public void setMinValue(Double minValue) {
            this.minValue = minValue;
        }

        public Double getMaxValue() {
            return maxValue;
        }

        public void setMaxValue(Double maxValue) {
            this.maxValue = maxValue;
        }

        public Double getGrowthRate() {
            return growthRate;
        }

        public void setGrowthRate(Double growthRate) {
            this.growthRate = growthRate;
        }

        public String getTrend() {
            return trend;
        }

        public void setTrend(String trend) {
            this.trend = trend;
        }
    }

    // Constructors
    public AnalyticsResponseDTO() {}

    public AnalyticsResponseDTO(String metricType, String periodType, LocalDate startDate, 
                              LocalDate endDate, List<DataPoint> dataPoints, Summary summary) {
        this.metricType = metricType;
        this.periodType = periodType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dataPoints = dataPoints;
        this.summary = summary;
    }

    // Getters and Setters
    public String getMetricType() {
        return metricType;
    }

    public void setMetricType(String metricType) {
        this.metricType = metricType;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public Map<String, Object> getAdditionalMetrics() {
        return additionalMetrics;
    }

    public void setAdditionalMetrics(Map<String, Object> additionalMetrics) {
        this.additionalMetrics = additionalMetrics;
    }
}
