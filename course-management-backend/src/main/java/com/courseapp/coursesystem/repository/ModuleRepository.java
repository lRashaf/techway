package com.courseapp.coursesystem.repository;

import com.courseapp.coursesystem.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    List<Module> findByCourseIdOrderByOrderIndexAsc(Long courseId);


}
