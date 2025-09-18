package com.courseapp.coursesystem.repository;

import com.courseapp.coursesystem.entity.Review;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCourse(Course course);
    List<Review> findByCourseId(Long courseId);
    List<Review> findByUser(User user);
    List<Review> findByUserId(Long userId);
    boolean existsByUserAndCourse(User user, Course course);
    Optional<Review> findByUserAndCourse(User user, Course course);
    List<Review> findByCourseIdAndRating(Long courseId, Integer rating);
    List<Review> findByRatingGreaterThanEqual(Integer rating);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.id = :courseId")
    Double findAverageRatingByCourseId(Long courseId);

    long countByCourseId(Long courseId);
    List<Review> findTop5ByOrderByCreatedAtDesc();
    List<Review> findTop5ByCourseIdOrderByCreatedAtDesc(Long courseId);

    // ====== جديد: حذف جماعي لكل مراجعات كورس ======
    @Modifying
    @Transactional
    void deleteByCourseId(Long courseId);
}
