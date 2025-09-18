package com.courseapp.coursesystem.repository;

import com.courseapp.coursesystem.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    // البحث برقم الشهادة
    Optional<Certificate> findByCertificateNumber(String certificateNumber);

    // شهادات المستخدم
    List<Certificate> findByUserIdOrderByIssuedAtDesc(Long userId);

    // شهادة مستخدم لكورس معين
    Optional<Certificate> findByUserIdAndCourseId(Long userId, Long courseId);

    // شهادات كورس معين
    List<Certificate> findByCourseIdOrderByIssuedAtDesc(Long courseId);

    // التحقق من وجود شهادة
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    // جلب الشهادات مع بيانات المستخدم والكورس
    @Query("SELECT c FROM Certificate c LEFT JOIN FETCH c.user LEFT JOIN FETCH c.course WHERE c.id = :certificateId")
    Optional<Certificate> findByIdWithUserAndCourse(@Param("certificateId") Long certificateId);

    // إحصائيات الشهادات
    @Query("SELECT COUNT(c), AVG(c.finalScore) FROM Certificate c WHERE c.course.id = :courseId")
    Object[] getCourseStatistics(@Param("courseId") Long courseId);

    // حذف شهادات كورس معين
    void deleteByCourseId(Long courseId);

    // حذف شهادات مستخدم معين
    void deleteByUserId(Long userId);
}