package com.courseapp.coursesystem.repository;

import com.courseapp.coursesystem.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByModuleIdOrderByOrderIndexAsc(Long moduleId);
    List<Lesson> findByModuleCourseIdOrderByModuleOrderIndexAscOrderIndexAsc(Long courseId);

    List<Lesson> findByModuleCourseId(Long courseId);
}
