package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.Module;
import com.courseapp.coursesystem.exception.CourseNotFoundException;
import com.courseapp.coursesystem.exception.ValidationException;
import com.courseapp.coursesystem.repository.CourseRepository;
import com.courseapp.coursesystem.repository.ModuleRepository;
import com.courseapp.coursesystem.repository.FavoriteRepository;
import com.courseapp.coursesystem.repository.ReviewRepository;
import com.courseapp.coursesystem.repository.EnrollmentRepository;
import com.courseapp.coursesystem.repository.LessonProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    // ====== جديد: نحتاج هذه الريبو للتنظيف قبل حذف الكورس ======
    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Optional<Course> getCourseById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Course ID must be a positive number");
        }
        return courseRepository.findById(id);
    }

    @Override
    public Course createCourse(Course course) {
        // التحقق من البيانات الأساسية
        validateCourse(course);

        // إذا الكورس مجاني، نخلي السعر = 0
        if (Boolean.TRUE.equals(course.getIsFree())) {
            course.setPrice(java.math.BigDecimal.ZERO);
        }

        // التحقق من السعر
        if (Boolean.FALSE.equals(course.getIsFree()) &&
                (course.getPrice() == null || course.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0)) {
            throw new ValidationException("Paid courses must have a price greater than 0");
        }

        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Long id, Course course) {
        if (id == null || id <= 0) {
            throw new ValidationException("Course ID must be a positive number");
        }

        Optional<Course> existingCourse = courseRepository.findById(id);

        if (existingCourse.isEmpty()) {
            throw new CourseNotFoundException(id);
        }

        // التحقق من البيانات الجديدة
        validateCourse(course);

        Course courseToUpdate = existingCourse.get();

        // تحديث البيانات
        courseToUpdate.setTitle(course.getTitle());
        courseToUpdate.setDescription(course.getDescription());
        courseToUpdate.setLocation(course.getLocation());
        courseToUpdate.setDuration(course.getDuration());
        courseToUpdate.setPrice(course.getPrice());
        courseToUpdate.setIsFree(course.getIsFree());
        courseToUpdate.setInstructor(course.getInstructor());
        courseToUpdate.setImageUrl(course.getImageUrl()); // ← الإضافة الجديدة

        // إذا الكورس مجاني، نخلي السعر = 0
        if (Boolean.TRUE.equals(course.getIsFree())) {
            courseToUpdate.setPrice(java.math.BigDecimal.ZERO);
        }

        return courseRepository.save(courseToUpdate);
    }

    // ====== مهم: حذف مرتب، يمنع تضارب الـ FK ======
    @Override
    @Transactional
    public void deleteCourse(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Course ID must be a positive number");
        }

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        // 1) امسحي تقدّم الدروس المرتبط بكل دروس الكورس
        lessonProgressRepository.deleteByCourseId(id);

        // 2) امسحي المراجعات والمفضلة والتسجيلات
        reviewRepository.deleteByCourseId(id);
        favoriteRepository.deleteByCourseId(id);
        enrollmentRepository.deleteByCourseId(id);

        // 3) امسحي الموديولات واحدة واحدة (عشان يشتغل orphanRemoval للدروس تحتها)
        List<Module> modules = moduleRepository.findByCourseIdOrderByOrderIndexAsc(id);
        if (modules != null && !modules.isEmpty()) {
            moduleRepository.deleteAll(modules);
        }

        // 4) أحذف الكورس نفسه
        courseRepository.delete(course);
    }

    @Override
    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new ValidationException("Search keyword cannot be empty");
        }

        return courseRepository.findByTitleContainingIgnoreCase(keyword.trim());
    }

    @Override
    public List<Course> getCoursesByLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new ValidationException("Location cannot be empty");
        }

        return courseRepository.findByLocationContainingIgnoreCase(location.trim());
    }

    @Override
    public List<Course> getCoursesByType(Boolean isFree) {
        if (isFree == null) {
            throw new ValidationException("Course type (free/paid) must be specified");
        }

        return courseRepository.findByIsFree(isFree);
    }

    @Override
    public List<Course> searchAndFilterCourses(String keyword, String location, Boolean isFree) {
        // للحين نرجع جميع الكورسات - يمكن تحسينه لاحقاً
        return courseRepository.findAll();
    }

    // method مساعدة للتحقق من صحة البيانات
    private void validateCourse(Course course) {
        if (course == null) {
            throw new ValidationException("Course data is required");
        }

        if (course.getTitle() == null || course.getTitle().trim().isEmpty()) {
            throw new ValidationException("Course title is required");
        }

        if (course.getTitle().length() > 200) {
            throw new ValidationException("Course title cannot exceed 200 characters");
        }

        if (course.getInstructor() == null || course.getInstructor().trim().isEmpty()) {
            throw new ValidationException("Instructor name is required");
        }

        if (course.getInstructor().length() > 100) {
            throw new ValidationException("Instructor name cannot exceed 100 characters");
        }

        if (course.getDuration() != null && course.getDuration() <= 0) {
            throw new ValidationException("Course duration must be greater than 0");
        }

        if (course.getLocation() != null && course.getLocation().length() > 100) {
            throw new ValidationException("Location cannot exceed 100 characters");
        }
    }


}
