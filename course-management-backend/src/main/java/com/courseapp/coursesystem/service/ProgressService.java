package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.dto.CourseProgressView;
import com.courseapp.coursesystem.dto.LessonProgressUpsertRequest;

public interface ProgressService {
    CourseProgressView upsertLessonProgress(Long userId, LessonProgressUpsertRequest req);
    CourseProgressView getCourseProgress(Long userId, Long courseId);
}
