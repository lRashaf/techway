package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.Lesson;
import com.courseapp.coursesystem.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    public LessonServiceImpl(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public List<Lesson> findByModuleId(Long moduleId) {
        return lessonRepository.findByModuleIdOrderByOrderIndexAsc(moduleId);
    }

    @Override
    public List<Lesson> findByCourseId(Long courseId) {
        return lessonRepository.findByModuleCourseIdOrderByModuleOrderIndexAscOrderIndexAsc(courseId);
    }
}
