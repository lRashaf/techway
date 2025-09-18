package com.courseapp.coursesystem.repository;

import com.courseapp.coursesystem.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    List<Category> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c, SIZE(c.courses) as courseCount FROM Category c ORDER BY SIZE(c.courses) DESC")
    List<Object[]> findCategoriesWithCourseCount();

    @Query("SELECT DISTINCT c FROM Category c WHERE SIZE(c.courses) > 0")
    List<Category> findCategoriesWithCourses();

    List<Category> findByColor(String color);
}