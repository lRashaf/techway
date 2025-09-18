// src/main/java/com/courseapp/coursesystem/controller/AdminStatsController.java
package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.dto.AdminOverviewDTO;
import com.courseapp.coursesystem.dto.DistributionDTO;
import com.courseapp.coursesystem.dto.TrendDTO;
import com.courseapp.coursesystem.dto.TopCourseRowDTO;
import com.courseapp.coursesystem.dto.ActivityItemDTO;
import com.courseapp.coursesystem.exception.ApiResponse;
import com.courseapp.coursesystem.service.AdminStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @Autowired
    public AdminStatsController(AdminStatsService adminStatsService) {
        this.adminStatsService = adminStatsService;
    }

    // نظرة عامة للداشبورد (كروت + آخر كورسات)
    //@GetMapping("/dashboard")
    //public ResponseEntity<ApiResponse<AdminOverviewDTO>> overview() {
      //  AdminOverviewDTO dto = adminStatsService.getOverview();
       // return ResponseEntity.ok(new ApiResponse<>(dto, "Dashboard overview loaded"));
    //}

    // ترند التسجيلات حسب المدى (7d/30d/90d)
    @GetMapping("/stats/enrollments-trend")
    public ResponseEntity<ApiResponse<TrendDTO>> trend(@RequestParam(defaultValue = "30d") String range) {
        TrendDTO dto = adminStatsService.getEnrollmentsTrend(range);
        return ResponseEntity.ok(new ApiResponse<>(dto, "Enrollments trend loaded"));
    }

    // توزيع التسجيلات حسب فئة الكورس
    @GetMapping("/stats/course-distribution")
    public ResponseEntity<ApiResponse<DistributionDTO>> distribution() {
        DistributionDTO dto = adminStatsService.getCourseDistribution();
        return ResponseEntity.ok(new ApiResponse<>(dto, "Course distribution loaded"));
    }

    // أعلى الكورسات (عدد طلاب/إيراد)
    @GetMapping("/stats/top-courses")
    public ResponseEntity<ApiResponse<List<TopCourseRowDTO>>> topCourses(
            @RequestParam(defaultValue = "5") int limit
    ) {
        List<TopCourseRowDTO> rows = adminStatsService.getTopCourses(limit);
        return ResponseEntity.ok(new ApiResponse<>(rows, "Top courses loaded"));
    }

    // أحدث الأنشطة (تسجيل/إنشاء كورس)
    @GetMapping("/activity/recent")
    public ResponseEntity<ApiResponse<List<ActivityItemDTO>>> recent(
            @RequestParam(defaultValue = "8") int limit
    ) {
        List<ActivityItemDTO> items = adminStatsService.getRecentActivity(limit);
        return ResponseEntity.ok(new ApiResponse<>(items, "Recent activity loaded"));
    }
}
