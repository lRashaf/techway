package com.courseapp.coursesystem.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgressView {
    private Long courseId;
    private int percent; // إجمالي الكورس
    private List<ModuleProgressView> modules;
    private Map<Long, Integer> lessons; // lessonId -> %
}
