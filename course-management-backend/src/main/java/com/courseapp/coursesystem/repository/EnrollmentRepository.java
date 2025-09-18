package com.courseapp.coursesystem.repository;

import com.courseapp.coursesystem.entity.Enrollment;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // البحث باليوزر
    List<Enrollment> findByUser(User user);
    List<Enrollment> findByUserId(Long userId);

    // البحث بالكورس
    List<Enrollment> findByCourse(Course course);
    List<Enrollment> findByCourseId(Long courseId);

    // تحقق من التسجيل
    boolean existsByUserAndCourse(User user, Course course);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    // حسب الحالة
    List<Enrollment> findByStatus(String status);
    List<Enrollment> findByUserAndStatus(User user, String status);
    List<Enrollment> findByUserIdAndStatus(Long userId, String status);

    // واحد محدد
    Enrollment findByUserIdAndCourseId(Long userId, Long courseId);

    // ====== إضافات الدفع ======
    List<Enrollment> findByUserIdAndPaidTrue(Long userId);       // جميع الكورسات المدفوعة ليوزر
    List<Enrollment> findByCourseIdAndPaidTrue(Long courseId);   // كل اللي دفعوا على كورس
    long countByCourseIdAndPaidTrue(Long courseId);              // عدد المشترين لكورس
    Enrollment findByPaymentReference(String paymentReference);  // إيجاد التسجيل عن طريق مرجع الدفع

    // ====== جديد: حذف جماعي لكل تسجيلات كورس معيّن ======
    @Modifying
    @Transactional
    void deleteByCourseId(Long courseId);
}
