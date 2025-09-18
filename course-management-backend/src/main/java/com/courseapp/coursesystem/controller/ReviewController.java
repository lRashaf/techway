package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.entity.Review;
import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.exception.ApiResponse;
import com.courseapp.coursesystem.exception.ValidationException;
import com.courseapp.coursesystem.service.ReviewService;
import com.courseapp.coursesystem.service.CourseService;
import com.courseapp.coursesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    // جلب تقييمات كورس معين
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Review>>> getCourseReviews(@PathVariable Long courseId) {
        List<Review> reviews = reviewService.getReviewsByCourseId(courseId);

        ApiResponse<List<Review>> response = new ApiResponse<>(reviews, "Course reviews retrieved successfully");
        return ResponseEntity.ok(response);
    }

    // جلب تقييمات مستخدم معين
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Review>>> getUserReviews(@PathVariable Long userId) {
        List<Review> reviews = reviewService.getReviewsByUserId(userId);

        ApiResponse<List<Review>> response = new ApiResponse<>(reviews, "User reviews retrieved successfully");
        return ResponseEntity.ok(response);
    }

    // إضافة تقييم جديد
    @PostMapping
    public ResponseEntity<ApiResponse<Review>> createReview(@RequestBody CreateReviewRequest request) {
        // التحقق من وجود المستخدم والكورس
        Optional<User> user = userService.getUserById(request.getUserId());
        Optional<Course> course = courseService.getCourseById(request.getCourseId());

        if (user.isEmpty()) {
            throw new ValidationException("User not found");
        }
        if (course.isEmpty()) {
            throw new ValidationException("Course not found");
        }

        // التحقق من عدم وجود تقييم سابق من نفس المستخدم للكورس
        if (reviewService.hasUserReviewedCourse(request.getUserId(), request.getCourseId())) {
            throw new ValidationException("User has already reviewed this course");
        }

        Review review = new Review();
        review.setUser(user.get());
        review.setCourse(course.get());
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review createdReview = reviewService.createReview(review);

        ApiResponse<Review> response = new ApiResponse<>(createdReview, "Review created successfully");
        return ResponseEntity.ok(response);
    }

    // تحديث تقييم موجود
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Review>> updateReview(
            @PathVariable Long reviewId,
            @RequestBody UpdateReviewRequest request) {

        Optional<Review> existingReview = reviewService.getReviewById(reviewId);
        if (existingReview.isEmpty()) {
            throw new ValidationException("Review not found");
        }

        Review review = existingReview.get();

        // التحقق من أن المستخدم هو صاحب التقييم
        if (!review.getUser().getId().equals(request.getUserId())) {
            throw new ValidationException("You can only update your own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updatedReview = reviewService.updateReview(review);

        ApiResponse<Review> response = new ApiResponse<>(updatedReview, "Review updated successfully");
        return ResponseEntity.ok(response);
    }

    // حذف تقييم
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {

        Optional<Review> review = reviewService.getReviewById(reviewId);
        if (review.isEmpty()) {
            throw new ValidationException("Review not found");
        }

        // التحقق من أن المستخدم هو صاحب التقييم أو مدير
        User user = userService.getUserById(userId).orElseThrow(() -> new ValidationException("User not found"));
        if (!review.get().getUser().getId().equals(userId) && !"ADMIN".equals(user.getRole())) {
            throw new ValidationException("You can only delete your own reviews");
        }

        reviewService.deleteReview(reviewId);

        ApiResponse<Void> response = new ApiResponse<>("Review deleted successfully");
        return ResponseEntity.ok(response);
    }

    // إحصائيات التقييمات لكورس معين
    @GetMapping("/course/{courseId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseReviewStats(@PathVariable Long courseId) {
        Map<String, Object> stats = new HashMap<>();

        List<Review> reviews = reviewService.getReviewsByCourseId(courseId);

        if (reviews.isEmpty()) {
            stats.put("averageRating", 0.0);
            stats.put("totalReviews", 0);
            stats.put("ratingDistribution", new HashMap<Integer, Long>());
        } else {
            // متوسط التقييم
            double averageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            stats.put("averageRating", Math.round(averageRating * 10.0) / 10.0);

            // عدد التقييمات
            stats.put("totalReviews", reviews.size());

            // توزيع التقييمات (1-5 نجوم)
            Map<Integer, Long> distribution = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                final int rating = i;
                long count = reviews.stream().filter(r -> r.getRating() == rating).count();
                distribution.put(rating, count);
            }
            stats.put("ratingDistribution", distribution);
        }

        ApiResponse<Map<String, Object>> response = new ApiResponse<>(stats, "Course review statistics retrieved successfully");
        return ResponseEntity.ok(response);
    }

    // أحدث التقييمات
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<Review>>> getRecentReviews(@RequestParam(defaultValue = "10") int limit) {
        List<Review> recentReviews = reviewService.getRecentReviews(limit);

        ApiResponse<List<Review>> response = new ApiResponse<>(recentReviews, "Recent reviews retrieved successfully");
        return ResponseEntity.ok(response);
    }
}

// DTOs للطلبات
class CreateReviewRequest {
    private Long userId;
    private Long courseId;
    private Integer rating;
    private String comment;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}

class UpdateReviewRequest {
    private Long userId;
    private Integer rating;
    private String comment;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}