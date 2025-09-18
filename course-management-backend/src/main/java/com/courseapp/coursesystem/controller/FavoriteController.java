package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.entity.Favorite;
import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.exception.ApiResponse;
import com.courseapp.coursesystem.exception.ValidationException;
import com.courseapp.coursesystem.service.FavoriteService;
import com.courseapp.coursesystem.service.CourseService;
import com.courseapp.coursesystem.service.FavoriteServiceImpl;
import com.courseapp.coursesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    private FavoriteServiceImpl favoriteService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    // جلب مفضلة المستخدم
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Course>>> getUserFavorites(@PathVariable Long userId) {
        List<Course> favoriteCourses = favoriteService.getUserFavoriteCourses(userId);

        ApiResponse<List<Course>> response = new ApiResponse<>(favoriteCourses, "User favorites retrieved successfully");
        return ResponseEntity.ok(response);
    }

    // إضافة كورس للمفضلة
    @PostMapping
    public ResponseEntity<ApiResponse<Favorite>> addToFavorites(@RequestBody AddFavoriteRequest request) {
        // التحقق من وجود المستخدم والكورس
        Optional<User> user = userService.getUserById(request.getUserId());
        Optional<Course> course = courseService.getCourseById(request.getCourseId());

        if (user.isEmpty()) {
            throw new ValidationException("User not found");
        }
        if (course.isEmpty()) {
            throw new ValidationException("Course not found");
        }

        // التحقق من عدم وجود الكورس في المفضلة مسبقاً
        if (favoriteService.isCourseInUserFavorites(request.getUserId(), request.getCourseId())) {
            throw new ValidationException("Course is already in favorites");
        }

        Favorite favorite = new Favorite(user.get(), course.get());
        Favorite createdFavorite = favoriteService.addToFavorites(favorite);

        ApiResponse<Favorite> response = new ApiResponse<>(createdFavorite, "Course added to favorites successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addFavorite(@RequestParam Long userId, @RequestParam Long courseId) {
        Favorite favorite = favoriteService.addFavorite(userId, courseId);
        return ResponseEntity.ok(favorite);
    }

    // إزالة كورس من المفضلة
    @DeleteMapping("/user/{userId}/course/{courseId}")
    public ResponseEntity<ApiResponse<Void>> removeFromFavorites(
            @PathVariable Long userId,
            @PathVariable Long courseId) {

        // التحقق من وجود المفضلة
        if (!favoriteService.isCourseInUserFavorites(userId, courseId)) {
            throw new ValidationException("Course is not in favorites");
        }

        favoriteService.removeFromFavorites(userId, courseId);

        ApiResponse<Void> response = new ApiResponse<>("Course removed from favorites successfully");
        return ResponseEntity.ok(response);
    }

    // التحقق من وجود كورس في المفضلة
    @GetMapping("/user/{userId}/course/{courseId}/check")
    public ResponseEntity<ApiResponse<Boolean>> checkIfFavorite(
            @PathVariable Long userId,
            @PathVariable Long courseId) {

        boolean isFavorite = favoriteService.isCourseInUserFavorites(userId, courseId);

        ApiResponse<Boolean> response = new ApiResponse<>(isFavorite,
                isFavorite ? "Course is in favorites" : "Course is not in favorites");
        return ResponseEntity.ok(response);
    }

    // جلب عدد المفضلات للمستخدم
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ApiResponse<Long>> getUserFavoritesCount(@PathVariable Long userId) {
        long count = favoriteService.getUserFavoritesCount(userId);

        ApiResponse<Long> response = new ApiResponse<>(count, "User favorites count retrieved successfully");
        return ResponseEntity.ok(response);
    }

    // جلب عدد المستخدمين الذين أضافوا كورس للمفضلة
    @GetMapping("/course/{courseId}/count")
    public ResponseEntity<ApiResponse<Long>> getCourseFavoritesCount(@PathVariable Long courseId) {
        long count = favoriteService.getCourseFavoritesCount(courseId);

        ApiResponse<Long> response = new ApiResponse<>(count, "Course favorites count retrieved successfully");
        return ResponseEntity.ok(response);
    }

    // الكورسات الأكثر إضافة للمفضلة
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<Course>>> getMostFavoritedCourses() {
        List<Course> popularCourses = favoriteService.getMostFavoritedCourses();

        ApiResponse<List<Course>> response = new ApiResponse<>(popularCourses, "Most favorited courses retrieved successfully");
        return ResponseEntity.ok(response);
    }

    // تبديل حالة المفضلة (إضافة/إزالة)
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<Boolean>> toggleFavorite(@RequestBody ToggleFavoriteRequest request) {
        // التحقق من وجود المستخدم والكورس
        Optional<User> user = userService.getUserById(request.getUserId());
        Optional<Course> course = courseService.getCourseById(request.getCourseId());

        if (user.isEmpty()) {
            throw new ValidationException("User not found");
        }
        if (course.isEmpty()) {
            throw new ValidationException("Course not found");
        }

        boolean isCurrentlyFavorite = favoriteService.isCourseInUserFavorites(request.getUserId(), request.getCourseId());

        if (isCurrentlyFavorite) {
            // إزالة من المفضلة
            favoriteService.removeFromFavorites(request.getUserId(), request.getCourseId());
            ApiResponse<Boolean> response = new ApiResponse<>(false, "Course removed from favorites");
            return ResponseEntity.ok(response);
        } else {
            // إضافة للمفضلة
            Favorite favorite = new Favorite(user.get(), course.get());
            favoriteService.addToFavorites(favorite);
            ApiResponse<Boolean> response = new ApiResponse<>(true, "Course added to favorites");
            return ResponseEntity.ok(response);
        }
    }
}

// DTOs للطلبات
class AddFavoriteRequest {
    private Long userId;
    private Long courseId;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
}

class ToggleFavoriteRequest {
    private Long userId;
    private Long courseId;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
}