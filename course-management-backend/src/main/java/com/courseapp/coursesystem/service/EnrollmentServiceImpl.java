package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.Enrollment;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.repository.CourseRepository;
import com.courseapp.coursesystem.repository.EnrollmentRepository;
import com.courseapp.coursesystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public List<Enrollment> getEnrollmentsByUserId(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    @Override
    public Enrollment enrollUserInCourse(User user, Course course) {
        // Idempotent: إن كان موجود رجّعه
        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            return enrollmentRepository.findByUserIdAndCourseId(user.getId(), course.getId());
        }

        Enrollment e = new Enrollment();
        e.setUser(user);
        e.setCourse(course);
        e.setStatus("ACTIVE");
        e.setProgress(BigDecimal.ZERO);
        e.setEnrolledAt(LocalDateTime.now());

        // مجاني = مدفوع تلقائيًا
        boolean isFree = isCourseFree(course);
        if (isFree) {
            e.setPaid(true);
            e.setPaidAt(LocalDateTime.now());
        }

        return enrollmentRepository.save(e);
    }

    @Override
    public Enrollment enrollUserInCourse(Long userId, Long courseId) {
        // Idempotent: إن كان موجود رجّعه
        Enrollment existing = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (existing != null) {
            // لو الكورس مجاني وماهو معلم paid لأي سبب، علّمه الآن
            if (!existing.isPaid() && isCourseFree(existing.getCourse())) {
                existing.setPaid(true);
                existing.setPaidAt(LocalDateTime.now());
                return enrollmentRepository.save(existing);
            }
            return existing;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        return enrollUserInCourse(user, course);
    }

    @Override
    public Enrollment markPaid(Long userId, Long courseId, String paymentReference) {
        Enrollment e = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (e == null) {
            // لو وصل تأكيد دفع بدون تسجيل مسبق، ننشئ Enrollment ثم نعلّمه مدفوع
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));
            e = enrollUserInCourse(user, course);
        }
        if (!e.isPaid()) {
            e.setPaid(true);
            e.setPaidAt(LocalDateTime.now());
        }
        if (paymentReference != null && !paymentReference.isBlank()) {
            e.setPaymentReference(paymentReference);
        }
        return enrollmentRepository.save(e);
    }

    @Override
    public Map<String, Object> getAccessView(Long userId, Long courseId) {
        Map<String, Object> m = new HashMap<>();
        Enrollment e = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        boolean enrolled = (e != null);
        boolean paid = enrolled && e.isPaid();

        m.put("enrolled", enrolled);
        m.put("paid", paid);
        m.put("status", paid ? "PAID" : (enrolled ? "ENROLLED_UNPAID" : "NOT_ENROLLED"));
        m.put("freeModules", 2); // أول وحدتين مجانًا دائمًا في المدفوع

        return m;
    }

    // ===== Helpers =====

    private boolean isCourseFree(Course course) {
        // اعتبر الكورس مجاني إذا السعر null أو <= 0
        try {
            BigDecimal price = (BigDecimal) Course.class.getMethod("getPrice").invoke(course);
            return (price == null || price.compareTo(BigDecimal.ZERO) <= 0);
        } catch (ReflectiveOperationException ignored) {
            // لو ما عنده getPrice، افترض غير مجاني
            return false;
        }
    }
}
