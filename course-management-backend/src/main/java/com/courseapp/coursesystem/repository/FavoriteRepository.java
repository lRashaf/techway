package com.courseapp.coursesystem.repository;

import com.courseapp.coursesystem.entity.Favorite;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

// ↓↓↓ إضافات للحذف الجماعي ↓↓↓
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
// ↑↑↑

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // جلب مفضلة المستخدم
    List<Favorite> findByUserId(Long userId);
    List<Favorite> findByUser(User user);

    // جلب المستخدمين الذين أضافوا كورس للمفضلة
    List<Favorite> findByCourseId(Long courseId);
    List<Favorite> findByCourse(Course course);

    // التحقق من وجود المفضلة
    boolean existsByUserAndCourse(User user, Course course);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    // جلب مفضلة معينة
    Optional<Favorite> findByUserAndCourse(User user, Course course);
    Optional<Favorite> findByUserIdAndCourseId(Long userId, Long courseId);

    // حذف مفضلة
    void deleteByUserAndCourse(User user, Course course);
    void deleteByUserIdAndCourseId(Long userId, Long courseId);

    // إحصائيات
    long countByUserId(Long userId);
    long countByCourseId(Long courseId);

    // أحدث المفضلات
    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    // الكورسات الأكثر إضافة للمفضلة
    @Query("SELECT f.course, COUNT(f) as favCount FROM Favorite f GROUP BY f.course ORDER BY favCount DESC")
    List<Object[]> findMostFavoritedCourses();

    // ====== جديد: حذف كل المفضلات التابعة لكورس محدد ======
    @Modifying
    @Transactional
    void deleteByCourseId(Long courseId);
}
