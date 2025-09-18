package com.courseapp.coursesystem.mapper;

import com.courseapp.coursesystem.dto.CourseOutlineDto;
import com.courseapp.coursesystem.dto.LessonDto;
import com.courseapp.coursesystem.dto.ModuleDto;
import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.Lesson;
import com.courseapp.coursesystem.entity.Module;

import java.util.List;
import java.util.stream.Collectors;

public class OutlineMapper {

    private OutlineMapper() {}

    public static CourseOutlineDto toCourseOutlineDto(Course course, List<Module> modules, boolean lockPaidModules) {
        CourseOutlineDto dto = new CourseOutlineDto(
                course.getId(),
                course.getTitle(),
                course.getIsFree(),
                course.getLocationType(),
                0.0 // سنحسب لاحقًا مع التقدم
        );

        List<ModuleDto> moduleDtos = modules.stream().map(m -> {
            ModuleDto md = new ModuleDto(
                    m.getId(),
                    m.getTitle(),
                    m.getSummary(),
                    m.getOrderIndex(),
                    m.getIsFree(),
                    null // progress per module لاحقًا
            );

            boolean isModuleLocked = lockPaidModules && !Boolean.TRUE.equals(m.getIsFree());

            List<LessonDto> lessonDtos = m.getLessons().stream()
                    .sorted((a, b) -> Integer.compare(
                            a.getOrderIndex() != null ? a.getOrderIndex() : 0,
                            b.getOrderIndex() != null ? b.getOrderIndex() : 0))
                    .map(l -> toLessonDto(l, isModuleLocked))
                    .collect(Collectors.toList());

            md.setLessons(lessonDtos);
            return md;
        }).collect(Collectors.toList());

        dto.setModules(moduleDtos);
        return dto;
    }

    private static LessonDto toLessonDto(Lesson lesson, boolean isModuleLocked) {
        // لو الموديول مقفول نخلي الدرس مقفول، وإلا مفتوح (مع isPreview معلومة مفيدة للـ UI)
        boolean isLocked = isModuleLocked;
        LessonDto dto = new LessonDto(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getOrderIndex(),
                lesson.getDurationMinutes(),
                lesson.getContentType(),
                isLocked,
                lesson.getIsPreview()
        );

        // إضافة videoUrl
        dto.setVideoUrl(lesson.getVideoUrl());

        return dto;
    }
}