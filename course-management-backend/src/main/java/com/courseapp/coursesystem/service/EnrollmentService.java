package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.Enrollment;
import com.courseapp.coursesystem.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface EnrollmentService {

    List<Enrollment> getEnrollmentsByUserId(Long userId);

    // موجودة من قبل – الآن لها تنفيذ فعلي
    Enrollment enrollUserInCourse(User user, Course course);

    // موجودة من قبل – تنادي البحث/الإنشاء idempotent
    Enrollment enrollUserInCourse(Long userId, Long courseId);

    // جديدة: تحديث حالة الدفع (يستدعيها PaymentService)
    Enrollment markPaid(Long userId, Long courseId, String paymentReference);

    // جديدة: تُستخدم في CourseDetails لتحديد القفل/الفتح
    Map<String, Object> getAccessView(Long userId, Long courseId);
}
