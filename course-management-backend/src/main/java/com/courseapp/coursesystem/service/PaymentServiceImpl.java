package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.Enrollment;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.repository.CourseRepository;
import com.courseapp.coursesystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final EnrollmentService enrollmentService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public PaymentServiceImpl(EnrollmentService enrollmentService,
                              UserRepository userRepository,
                              CourseRepository courseRepository) {
        this.enrollmentService = enrollmentService;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public String createPaymentSession(Long userId, Long courseId) {
        try {
            logger.info("=== Creating Payment Session ===");
            logger.info("User ID: {}", userId);
            logger.info("Course ID: {}", courseId);

            // تحقق من وجود المستخدم
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            logger.info("User found: {}", user.getName());

            // تحقق من وجود الكورس
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
            logger.info("Course found: {}", course.getTitle());

            // تأكيد وجود Enrollment قبل الدفع (Idempotent)
            Enrollment enrollment = enrollmentService.enrollUserInCourse(userId, courseId);
            logger.info("Enrollment created/found: ID {}, Paid: {}", enrollment.getId(), enrollment.isPaid());

            // إذا كان مدفوع مسبقاً، أرجع رابط النجاح مباشرة
            if (enrollment.isPaid()) {
                logger.info("Course already paid, redirecting to success");
                return "http://localhost:3000/courses/" + courseId + "?paid=1&already=true";
            }

            // إنشاء معرف فريد للدفعة (لاحقاً مع مزوّد الدفع الحقيقي)
            String paymentReference = "PAY_" + UUID.randomUUID().toString().substring(0, 8);
            logger.info("Generated payment reference: {}", paymentReference);

            // رابط محاكاة للدفع المحلي — يوجّه لنقطة النجاح
            String successUrl = "http://localhost:8080/api/payments/success?userId=" + userId
                    + "&courseId=" + courseId
                    + "&ref=" + paymentReference;

            logger.info("Payment session created successfully: {}", successUrl);
            return successUrl;

        } catch (Exception e) {
            logger.error("Error creating payment session for user {} and course {}: {}",
                    userId, courseId, e.getMessage(), e);
            throw new RuntimeException("Failed to create payment session: " + e.getMessage());
        }
    }

    @Override
    public boolean confirmPayment(Long userId, Long courseId) {
        try {
            logger.info("=== Confirming Payment ===");
            logger.info("User ID: {}", userId);
            logger.info("Course ID: {}", courseId);

            // تحقق من وجود المستخدم والكورس
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

            logger.info("Confirming payment for user: {} and course: {}", user.getName(), course.getTitle());

            // تثبيت الدفع (Idempotent) — يعلّم التسجيل كـ Paid
            String paymentReference = "CONFIRMED_" + LocalDateTime.now().toString();
            Enrollment enrollment = enrollmentService.markPaid(userId, courseId, paymentReference);

            boolean success = enrollment != null && enrollment.isPaid();
            logger.info("Payment confirmation result: {}", success);

            if (success) {
                logger.info("Payment confirmed successfully for enrollment ID: {}", enrollment.getId());
            } else {
                logger.error("Failed to confirm payment for user {} and course {}", userId, courseId);
            }

            return success;

        } catch (Exception e) {
            logger.error("Error confirming payment for user {} and course {}: {}",
                    userId, courseId, e.getMessage(), e);
            return false;
        }
    }

    // دالة مساعدة للتحقق من حالة الدفع
    public PaymentStatus getPaymentStatus(Long userId, Long courseId) {
        try {
            Enrollment enrollment = enrollmentService.getEnrollmentsByUserId(userId)
                    .stream()
                    .filter(e -> e.getCourse().getId().equals(courseId))
                    .findFirst()
                    .orElse(null);

            if (enrollment == null) {
                return new PaymentStatus(false, false, "غير مسجل");
            }

            return new PaymentStatus(
                    true,
                    enrollment.isPaid(),
                    enrollment.isPaid() ? "مدفوع" : "مسجل لكن غير مدفوع"
            );

        } catch (Exception e) {
            logger.error("Error checking payment status: {}", e.getMessage());
            return new PaymentStatus(false, false, "خطأ في التحقق");
        }
    }

    // كلاس مساعد لحالة الدفع
    public static class PaymentStatus {
        private final boolean enrolled;
        private final boolean paid;
        private final String message;

        public PaymentStatus(boolean enrolled, boolean paid, String message) {
            this.enrolled = enrolled;
            this.paid = paid;
            this.message = message;
        }

        public boolean isEnrolled() { return enrolled; }
        public boolean isPaid() { return paid; }
        public String getMessage() { return message; }
    }
}