package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.entity.Enrollment;
import com.courseapp.coursesystem.exception.ApiResponse;
import com.courseapp.coursesystem.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // 1) جميع تسجيلات مستخدم
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Enrollment>>> getUserEnrollments(@PathVariable Long userId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(enrollments, "User enrollments retrieved successfully"));
    }

    // 2) تسجيل مستخدم في كورس (Idempotent)
    @PostMapping
    public ResponseEntity<ApiResponse<Enrollment>> enrollUser(@RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        Long courseId = payload.get("courseId");
        Enrollment enrollment = enrollmentService.enrollUserInCourse(userId, courseId);
        return ResponseEntity.ok(new ApiResponse<>(enrollment, "Enrollment created successfully"));
    }

    // 3) حالة الوصول للكورس (لتحديد القفل/الفتح في الواجهة)
    //    يرجّع: enrolled, paid, status, freeModules=2
    @GetMapping("/access")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAccess(
            @RequestParam Long userId,
            @RequestParam Long courseId
    ) {
        Map<String, Object> view = enrollmentService.getAccessView(userId, courseId);
        return ResponseEntity.ok(new ApiResponse<>(view, "Access view"));
    }
}
