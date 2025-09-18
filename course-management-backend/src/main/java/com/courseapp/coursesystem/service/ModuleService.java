package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.Module;

import java.util.List;

public interface ModuleService {
    List<Module> findByCourseId(Long courseId);
}
