package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.entity.Enrollment;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.exception.ApiResponse;
import com.courseapp.coursesystem.repository.EnrollmentRepository;
import com.courseapp.coursesystem.repository.UserRepository;
import com.courseapp.coursesystem.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserStatsController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET /api/users/{userId}/stats
     * إحصائيات المستخدم للـ Dashboard
     */
    @GetMapping("/{userId}/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getUserStats(@PathVariable Long userId) {
        try {
            // جلب تسجيلات المستخدم
            List<Enrollment> enrollments = enrollmentService.getEnrollmentsByUserId(userId);

            // حساب الإحصائيات
            long totalEnrolled = enrollments.size();
            long completedCourses = enrollments.stream()
                    .filter(e -> "COMPLETED".equals(e.getStatus()) ||
                            (e.getProgress() != null && e.getProgress().compareTo(new BigDecimal("100")) >= 0))
                    .count();

            long paidCourses = enrollments.stream()
                    .filter(Enrollment::isPaid)
                    .count();

            // حساب ساعات الدراسة التقديرية (بناء على التقدم والمدة)
            double studyHours = enrollments.stream()
                    .mapToDouble(e -> {
                        if (e.getCourse() != null && e.getCourse().getDuration() != null && e.getProgress() != null) {
                            return e.getCourse().getDuration() * (e.getProgress().doubleValue() / 100.0);
                        }
                        return 0;
                    })
                    .sum();

            // حساب معدل الإكمال
            double completionRate = totalEnrolled > 0 ?
                    enrollments.stream()
                            .filter(e -> e.getProgress() != null)
                            .mapToDouble(e -> e.getProgress().doubleValue())
                            .average()
                            .orElse(0.0) : 0.0;

            // بناء الاستجابة
            UserStatsResponse stats = new UserStatsResponse();
            stats.setTotalEnrolled(totalEnrolled);
            stats.setCompletedCourses(completedCourses);
            stats.setPaidCourses(paidCourses);
            stats.setStudyHours((int) Math.round(studyHours));
            stats.setCompletionRate((int) Math.round(completionRate));

            return ResponseEntity.ok(new ApiResponse<>(stats, "User statistics retrieved successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Error retrieving user statistics: " + e.getMessage()));
        }
    }

    /**
     * GET /api/users/{userId}/active-courses
     * الكورسات النشطة للمستخدم مع التقدم
     */
    @GetMapping("/{userId}/active-courses")
    public ResponseEntity<ApiResponse<List<ActiveCourseResponse>>> getActiveCourses(@PathVariable Long userId) {
        try {
            List<Enrollment> activeEnrollments = enrollmentRepository.findByUserIdAndStatus(userId, "ACTIVE");

            List<ActiveCourseResponse> activeCourses = activeEnrollments.stream()
                    .map(enrollment -> {
                        ActiveCourseResponse courseResponse = new ActiveCourseResponse();
                        courseResponse.setId(enrollment.getCourse().getId());
                        courseResponse.setTitle(enrollment.getCourse().getTitle());
                        courseResponse.setInstructor(enrollment.getCourse().getInstructor());
                        courseResponse.setLocationType(enrollment.getCourse().getLocationType() != null ?
                                enrollment.getCourse().getLocationType().toString() : "ONLINE");
                        courseResponse.setProgress(enrollment.getProgress() != null ?
                                enrollment.getProgress().intValue() : 0);
                        courseResponse.setImageUrl(enrollment.getCourse().getImageUrl());
                        courseResponse.setLastAccessed(enrollment.getEnrolledAt());
                        courseResponse.setIsPaid(enrollment.isPaid());
                        return courseResponse;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse<>(activeCourses, "Active courses retrieved successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Error retrieving active courses: " + e.getMessage()));
        }
    }

    /**
     * GET /api/users/{userId}/dashboard
     * بيانات Dashboard كاملة في استدعاء واحد
     */
    @GetMapping("/{userId}/dashboard")
    public ResponseEntity<ApiResponse<UserDashboardResponse>> getUserDashboard(@PathVariable Long userId) {
        try {
            // التحقق من وجود المستخدم
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // جلب الإحصائيات
            ResponseEntity<ApiResponse<UserStatsResponse>> statsResponse = getUserStats(userId);
            UserStatsResponse stats = statsResponse.getBody().getData();

            // جلب الكورسات النشطة
            ResponseEntity<ApiResponse<List<ActiveCourseResponse>>> coursesResponse = getActiveCourses(userId);
            List<ActiveCourseResponse> activeCourses = coursesResponse.getBody().getData();

            // حساب أيام العضوية
            long daysSinceJoined = user.getCreatedAt() != null ?
                    ChronoUnit.DAYS.between(user.getCreatedAt().toLocalDate(), LocalDateTime.now().toLocalDate()) : 0;

            // بناء استجابة Dashboard
            UserDashboardResponse dashboard = new UserDashboardResponse();
            dashboard.setStats(stats);
            dashboard.setActiveCourses(activeCourses);
            dashboard.setDaysSinceJoined(daysSinceJoined);
            dashboard.setUser(new UserSummary(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt()));

            return ResponseEntity.ok(new ApiResponse<>(dashboard, "Dashboard data retrieved successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("Error retrieving dashboard data: " + e.getMessage()));
        }
    }
}

// ===== Response DTOs =====

class UserStatsResponse {
    private long totalEnrolled;
    private long completedCourses;
    private long paidCourses;
    private int studyHours;
    private int completionRate;

    // Constructors
    public UserStatsResponse() {}

    // Getters and Setters
    public long getTotalEnrolled() { return totalEnrolled; }
    public void setTotalEnrolled(long totalEnrolled) { this.totalEnrolled = totalEnrolled; }

    public long getCompletedCourses() { return completedCourses; }
    public void setCompletedCourses(long completedCourses) { this.completedCourses = completedCourses; }

    public long getPaidCourses() { return paidCourses; }
    public void setPaidCourses(long paidCourses) { this.paidCourses = paidCourses; }

    public int getStudyHours() { return studyHours; }
    public void setStudyHours(int studyHours) { this.studyHours = studyHours; }

    public int getCompletionRate() { return completionRate; }
    public void setCompletionRate(int completionRate) { this.completionRate = completionRate; }
}

class ActiveCourseResponse {
    private Long id;
    private String title;
    private String instructor;
    private String locationType;
    private int progress;
    private String imageUrl;
    private LocalDateTime lastAccessed;
    private boolean isPaid;

    // Constructors
    public ActiveCourseResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public String getLocationType() { return locationType; }
    public void setLocationType(String locationType) { this.locationType = locationType; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(LocalDateTime lastAccessed) { this.lastAccessed = lastAccessed; }

    public boolean getIsPaid() { return isPaid; }
    public void setIsPaid(boolean isPaid) { this.isPaid = isPaid; }
}

class UserSummary {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime joinedAt;

    public UserSummary(Long id, String name, String email, LocalDateTime joinedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.joinedAt = joinedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}

class UserDashboardResponse {
    private UserStatsResponse stats;
    private List<ActiveCourseResponse> activeCourses;
    private long daysSinceJoined;
    private UserSummary user;

    // Constructors
    public UserDashboardResponse() {}

    // Getters and Setters
    public UserStatsResponse getStats() { return stats; }
    public void setStats(UserStatsResponse stats) { this.stats = stats; }

    public List<ActiveCourseResponse> getActiveCourses() { return activeCourses; }
    public void setActiveCourses(List<ActiveCourseResponse> activeCourses) { this.activeCourses = activeCourses; }

    public long getDaysSinceJoined() { return daysSinceJoined; }
    public void setDaysSinceJoined(long daysSinceJoined) { this.daysSinceJoined = daysSinceJoined; }

    public UserSummary getUser() { return user; }
    public void setUser(UserSummary user) { this.user = user; }
}