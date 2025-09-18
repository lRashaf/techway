package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.dto.AdminOverviewDTO;
import com.courseapp.coursesystem.dto.AdminUserDTO;
import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.exception.ApiResponse;
import com.courseapp.coursesystem.service.AdminStatsService;
import com.courseapp.coursesystem.service.CourseService;
import com.courseapp.coursesystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CourseService courseService;
    private final UserService userService;
    private final AdminStatsService adminStatsService;

    public AdminController(CourseService courseService,
                           UserService userService,
                           AdminStatsService adminStatsService) {
        this.courseService = courseService;
        this.userService = userService;
        this.adminStatsService = adminStatsService;
    }

    // ================================
    // Dashboard (يستخدم AdminStatsService)
    // ================================
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminOverviewDTO>> getDashboardStats() {
        AdminOverviewDTO dto = adminStatsService.getOverview();
        return ResponseEntity.ok(new ApiResponse<>(dto, "Dashboard data retrieved successfully"));
    }

    // ================================
    // Course Management
    // ================================
    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<Course>>> getAllCoursesAdmin() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(new ApiResponse<>(courses, "Courses retrieved successfully"));
    }

    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<Course>> createCourseAdmin(@RequestBody Course course) {
        Course createdCourse = courseService.createCourse(course);
        return ResponseEntity.ok(new ApiResponse<>(createdCourse, "Course created successfully by admin"));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<Course>> updateCourseAdmin(@PathVariable Long id, @RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(id, course);
        return ResponseEntity.ok(new ApiResponse<>(updatedCourse, "Course updated successfully by admin"));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourseAdmin(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(new ApiResponse<>("Course deleted successfully by admin"));
    }

    // ================================
    // User Management (يرجع DTO خفيف)
    // ================================
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserDTO>>> getAllUsersAdmin() {
        List<User> users = userService.getAllUsers();
        List<AdminUserDTO> dto = users.stream()
                .map(AdminUserDTO::from)
                .toList();
        return ResponseEntity.ok(new ApiResponse<>(dto, "Users retrieved successfully"));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<AdminUserDTO>> updateUserRole(@PathVariable Long id,
                                                                    @RequestBody Map<String, String> request) {
        String newRole = request.get("role");
        User updatedUser = userService.updateUserRole(id, newRole);
        AdminUserDTO dto = AdminUserDTO.from(updatedUser);
        return ResponseEntity.ok(new ApiResponse<>(dto, "User role updated successfully"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserAdmin(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>("User deleted successfully by admin"));
    }

    // ================================
    // Advanced Analytics (تبقى مثل ما هي)
    // ================================
    @GetMapping("/analytics/courses")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseAnalytics() {
        List<Course> courses = courseService.getAllCourses();
        Map<String, Object> analytics = new HashMap<>();

        // توزيع حسب الموقع
        Map<String, Long> locationStats = courses.stream()
                .collect(Collectors.groupingBy(
                        Course::getLocation,
                        Collectors.counting()
                ));
        analytics.put("locationDistribution", locationStats);

        // متوسط السعر للكورسات المدفوعة
        double avgPrice = courses.stream()
                .filter(c -> !Boolean.TRUE.equals(c.getIsFree()))
                .map(Course::getPrice)
                .filter(Objects::nonNull)
                .mapToDouble(Number::doubleValue)
                .average()
                .orElse(0.0);
        analytics.put("averagePrice", avgPrice);

        // أغلى/أرخص كورس (مدفوع)
        courses.stream()
                .filter(c -> !Boolean.TRUE.equals(c.getIsFree()))
                .filter(c -> c.getPrice() != null)
                .max(Comparator.comparing(Course::getPrice))
                .ifPresent(c -> analytics.put("mostExpensive", c));

        courses.stream()
                .filter(c -> !Boolean.TRUE.equals(c.getIsFree()))
                .filter(c -> c.getPrice() != null)
                .min(Comparator.comparing(Course::getPrice))
                .ifPresent(c -> analytics.put("cheapest", c));

        return ResponseEntity.ok(new ApiResponse<>(analytics, "Course analytics retrieved successfully"));
    }

    @GetMapping("/analytics/users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserAnalytics() {
        List<User> users = userService.getAllUsers();
        Map<String, Object> analytics = new HashMap<>();

        // ترند التسجيلات حسب الشهر (أسماء الأشهر)
        Map<String, Long> registrationTrend = users.stream()
                .filter(u -> u.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        u -> u.getCreatedAt().getMonth().toString(),
                        Collectors.counting()
                ));
        analytics.put("registrationTrend", registrationTrend);

        // نمو المستخدمين
        analytics.put("totalUsers", users.size());
        analytics.put("newUsersThisMonth", users.stream()
                .filter(u -> u.getCreatedAt() != null &&
                        u.getCreatedAt().getMonth() == java.time.LocalDateTime.now().getMonth())
                .count());

        return ResponseEntity.ok(new ApiResponse<>(analytics, "User analytics retrieved successfully"));
    }
}
