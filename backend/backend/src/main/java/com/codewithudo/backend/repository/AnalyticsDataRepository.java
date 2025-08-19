package com.codewithudo.backend.repository;

import com.codewithudo.backend.entity.AnalyticsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnalyticsDataRepository extends JpaRepository<AnalyticsData, Long> {

    @Query("SELECT a FROM AnalyticsData a WHERE a.companyId = :companyId AND a.metricType = :metricType " +
           "AND a.date BETWEEN :startDate AND :endDate AND a.periodType = :periodType " +
           "ORDER BY a.date ASC")
    List<AnalyticsData> findByCompanyAndMetricTypeAndDateRange(
            @Param("companyId") Long companyId,
            @Param("metricType") AnalyticsData.MetricType metricType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("periodType") AnalyticsData.PeriodType periodType
    );

    @Query("SELECT a FROM AnalyticsData a WHERE a.companyId = :companyId " +
           "AND a.date BETWEEN :startDate AND :endDate AND a.periodType = :periodType " +
           "ORDER BY a.date ASC")
    List<AnalyticsData> findByCompanyAndDateRange(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("periodType") AnalyticsData.PeriodType periodType
    );

    @Query("SELECT a FROM AnalyticsData a WHERE a.companyId = :companyId AND a.departmentId = :departmentId " +
           "AND a.date BETWEEN :startDate AND :endDate AND a.periodType = :periodType " +
           "ORDER BY a.date ASC")
    List<AnalyticsData> findByCompanyAndDepartmentAndDateRange(
            @Param("companyId") Long companyId,
            @Param("departmentId") Long departmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("periodType") AnalyticsData.PeriodType periodType
    );

    @Query("SELECT a FROM AnalyticsData a WHERE a.companyId = :companyId " +
           "AND a.metricType IN :metricTypes AND a.date = :date " +
           "AND a.periodType = :periodType")
    List<AnalyticsData> findByCompanyAndMetricTypesAndDate(
            @Param("companyId") Long companyId,
            @Param("metricTypes") List<AnalyticsData.MetricType> metricTypes,
            @Param("date") LocalDate date,
            @Param("periodType") AnalyticsData.PeriodType periodType
    );

    @Query("SELECT a FROM AnalyticsData a WHERE a.companyId = :companyId " +
           "AND a.metricType = :metricType AND a.periodType = :periodType " +
           "ORDER BY a.date DESC LIMIT 1")
    AnalyticsData findLatestByCompanyAndMetricType(
            @Param("companyId") Long companyId,
            @Param("metricType") AnalyticsData.MetricType metricType,
            @Param("periodType") AnalyticsData.PeriodType periodType
    );

    @Query("SELECT DISTINCT a.metricType FROM AnalyticsData a WHERE a.companyId = :companyId")
    List<AnalyticsData.MetricType> findAvailableMetricsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT a FROM AnalyticsData a WHERE a.companyId = :companyId " +
           "AND a.date >= :startDate ORDER BY a.date DESC")
    List<AnalyticsData> findRecentDataByCompany(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDate startDate
    );

    @Query(value = "SELECT * FROM analytics_data WHERE company_id = :companyId " +
           "AND metric_type = :metricType AND date BETWEEN :startDate AND :endDate " +
           "AND period_type = :periodType ORDER BY date ASC", nativeQuery = true)
    List<AnalyticsData> findNativeByCompanyAndMetricTypeAndDateRange(
            @Param("companyId") Long companyId,
            @Param("metricType") String metricType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("periodType") String periodType
    );
}
