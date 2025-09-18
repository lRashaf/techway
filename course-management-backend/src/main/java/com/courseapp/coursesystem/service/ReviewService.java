package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewService {

    // إدارة التقييمات
    Review createReview(Review review);
    Review updateReview(Review review);
    void deleteReview(Long reviewId);
    Optional<Review> getReviewById(Long reviewId);

    // جلب التقييمات
    List<Review> getReviewsByCourseId(Long courseId);
    List<Review> getReviewsByUserId(Long userId);
    List<Review> getAllReviews();

    // تحقق من وجود تقييم
    boolean hasUserReviewedCourse(Long userId, Long courseId);
    Optional<Review> getUserReviewForCourse(Long userId, Long courseId);

    // إحصائيات
    Double getAverageRatingForCourse(Long courseId);
    Long getReviewCountForCourse(Long courseId);
    List<Review> getRecentReviews(int limit);

    // تصفية التقييمات
    List<Review> getReviewsByRating(Integer rating);
    List<Review> getReviewsByCourseIdAndRating(Long courseId, Integer rating);
}