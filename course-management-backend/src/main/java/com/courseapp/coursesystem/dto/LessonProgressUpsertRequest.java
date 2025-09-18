package com.courseapp.coursesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressUpsertRequest {
    private Long lessonId;
    private Integer progressPercent; // 0..100
}
