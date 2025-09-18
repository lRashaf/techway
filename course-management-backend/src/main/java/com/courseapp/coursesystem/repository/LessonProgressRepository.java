package com.courseapp.coursesystem.repository;

import com.courseapp.coursesystem.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    @Query("select lp from LessonProgress lp where lp.user.id = :userId and lp.lesson.module.course.id = :courseId")
    List<LessonProgress> findByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    // ====== جديد: حذف كل تقدّم الدروس المرتبط بكورس معيّن (عبر السلسلة Lesson -> Module -> Course)
    @Modifying
    @Transactional
    @Query("delete from LessonProgress lp where lp.lesson.module.course.id = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);
}
