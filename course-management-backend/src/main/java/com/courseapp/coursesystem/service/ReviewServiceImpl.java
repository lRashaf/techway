package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.Review;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.exception.ValidationException;
import com.courseapp.coursesystem.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Review createReview(Review review) {
        validateReview(review);
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Review review) {
        validateReview(review);

        if (review.getId() == null) {
            throw new ValidationException("Review ID is required for update");
        }

        if (!reviewRepository.existsById(review.getId())) {
            throw new ValidationException("Review not found with id: " + review.getId());
        }

        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        if (reviewId == null || reviewId <= 0) {
            throw new ValidationException("Review ID must be a positive number");
        }

        if (!reviewRepository.existsById(reviewId)) {
            throw new ValidationException("Review not found with id: " + reviewId);
        }

        reviewRepository.deleteById(reviewId);
    }

    @Override
    public Optional<Review> getReviewById(Long reviewId) {
        if (reviewId == null || reviewId <= 0) {
            throw new ValidationException("Review ID must be a positive number");
        }
        return reviewRepository.findById(reviewId);
    }

    @Override
    public List<Review> getReviewsByCourseId(Long courseId) {
        if (courseId == null || courseId <= 0) {
            throw new ValidationException("Course ID must be a positive number");
        }
        return reviewRepository.findByCourseId(courseId);
    }

    @Override
    public List<Review> getReviewsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("User ID must be a positive number");
        }
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public boolean hasUserReviewedCourse(Long userId, Long courseId) {
        if (userId == null || userId <= 0 || courseId == null || courseId <= 0) {
            return false;
        }

        User user = new User();
        user.setId(userId);
        Course course = new Course();
        course.setId(courseId);

        return reviewRepository.existsByUserAndCourse(user, course);
    }

    @Override
    public Optional<Review> getUserReviewForCourse(Long userId, Long courseId) {
        if (userId == null || userId <= 0 || courseId == null || courseId <= 0) {
            return Optional.empty();
        }

        User user = new User();
        user.setId(userId);
        Course course = new Course();
        course.setId(courseId);

        return reviewRepository.findByUserAndCourse(user, course);
    }

    @Override
    public Double getAverageRatingForCourse(Long courseId) {
        if (courseId == null || courseId <= 0) {
            return 0.0;
        }
        return reviewRepository.findAverageRatingByCourseId(courseId);
    }

    @Override
    public Long getReviewCountForCourse(Long courseId) {
        if (courseId == null || courseId <= 0) {
            return 0L;
        }
        return reviewRepository.countByCourseId(courseId);
    }

    @Override
    public List<Review> getRecentReviews(int limit) {
        if (limit <= 0) {
            limit = 10; // قيمة افتراضية
        }
        return reviewRepository.findTop5ByOrderByCreatedAtDesc();
    }

    @Override
    public List<Review> getReviewsByRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }
        return reviewRepository.findByRatingGreaterThanEqual(rating);
    }

    @Override
    public List<Review> getReviewsByCourseIdAndRating(Long courseId, Integer rating) {
        if (courseId == null || courseId <= 0) {
            throw new ValidationException("Course ID must be a positive number");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }
        return reviewRepository.findByCourseIdAndRating(courseId, rating);
    }

    // التحقق من صحة التقييم
    private void validateReview(Review review) {
        if (review == null) {
            throw new ValidationException("Review data is required");
        }

        if (review.getUser() == null || review.getUser().getId() == null) {
            throw new ValidationException("User is required");
        }

        if (review.getCourse() == null || review.getCourse().getId() == null) {
            throw new ValidationException("Course is required");
        }

        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }

        // التعليق اختياري، لكن إذا كان موجود يجب ألا يكون فارغ
        if (review.getComment() != null && review.getComment().trim().isEmpty()) {
            review.setComment(null);
        }

        // حد أقصى لطول التعليق
        if (review.getComment() != null && review.getComment().length() > 1000) {
            throw new ValidationException("Comment cannot exceed 1000 characters");
        }
    }
}