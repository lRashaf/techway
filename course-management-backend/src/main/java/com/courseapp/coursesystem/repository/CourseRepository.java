package com.courseapp.coursesystem.repository;

import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // البحث بالموقع (نص)
    List<Course> findByLocationContainingIgnoreCase(String location);

    // البحث بنوع الكورس (مجاني/مدفوع)
    List<Course> findByIsFree(Boolean isFree);

    // البحث بالعنوان
    List<Course> findByTitleContainingIgnoreCase(String title);

    // البحث بالمدرب
    List<Course> findByInstructorContainingIgnoreCase(String instructor);

    // ✨ البحث بنوع الموقع (ONLINE, PHYSICAL, HYBRID)
    List<Course> findByLocationType(LocationType locationType);

    // ✨ جلب الكورسات اللي لها إحداثيات (lat/long مو null)
    List<Course> findByLatitudeIsNotNullAndLongitudeIsNotNull();
}
