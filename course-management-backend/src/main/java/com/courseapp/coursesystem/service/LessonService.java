package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.Lesson;

import java.util.List;

public interface LessonService {
    List<Lesson> findByModuleId(Long moduleId);
    List<Lesson> findByCourseId(Long courseId);
}
