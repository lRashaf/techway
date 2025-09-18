package com.courseapp.coursesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleProgressView {
    private Long moduleId;
    private int percent;
}
