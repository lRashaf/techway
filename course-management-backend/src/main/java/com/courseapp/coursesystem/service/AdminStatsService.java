package com.courseapp.coursesystem.service;


import com.courseapp.coursesystem.dto.*;

import java.util.List;

public interface AdminStatsService {

    // للأعلى: الكروت + آخر الكورسات
    AdminOverviewDTO getOverview();

    // خط التسجيلات حسب المدة (7d/30d/90d)
    TrendDTO getEnrollmentsTrend(String rangeKey);

    // توزيع التسجيلات حسب فئة الكورس (category) أو يرجّع "غير مصنّف" لو فاضي
    DistributionDTO getCourseDistribution();

    // أعلى الكورسات حسب عدد الطلاب ثم الإيراد
    List<TopCourseRowDTO> getTopCourses(int limit);

    // أحدث الأنشطة (تسجيل/إنشاء كورس)
    List<ActivityItemDTO> getRecentActivity(int limit);
}
