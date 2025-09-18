package com.courseapp.coursesystem.repository;

import com.courseapp.coursesystem.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // البحث عن كويز بـ ID الكورس
    Optional<Quiz> findByCourseId(Long courseId);

    // جلب جميع الكويزات لكورس معين
    List<Quiz> findAllByCourseId(Long courseId);

    // حذف كويز بـ ID الكورس
    void deleteByCourseId(Long courseId);

    // جلب كويز مع الأسئلة فقط (بدون خيارات)
    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :quizId")
    Optional<Quiz> findByIdWithQuestions(@Param("quizId") Long quizId);

    // جلب كويز الكورس مع الأسئلة فقط (تجنب MultipleBagFetchException)
    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.course.id = :courseId")
    Optional<Quiz> findByCourseIdWithQuestions(@Param("courseId") Long courseId);

    // طريقة جديدة: جلب الكويز مع الأسئلة والخيارات بطريقة آمنة
    @Query("SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :quizId")
    Optional<Quiz> findByIdWithQuestionsAndOptions(@Param("quizId") Long quizId);

    // طريقة جديدة: جلب كويز الكورس مع الأسئلة والخيارات بطريقة آمنة
    @Query("SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.course.id = :courseId")
    Optional<Quiz> findByCourseIdWithQuestionsAndOptions(@Param("courseId") Long courseId);

    // البحث بالعنوان
    List<Quiz> findByTitleContainingIgnoreCase(String title);

    // عدد الكويزات لكورس معين
    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
}