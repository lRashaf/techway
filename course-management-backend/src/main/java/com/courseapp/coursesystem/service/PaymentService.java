package com.courseapp.coursesystem.service;

public interface PaymentService {

    /**
     * إنشاء جلسة دفع للمستخدم والكورس
     * @param userId معرف المستخدم
     * @param courseId معرف الكورس
     * @return رابط الدفع أو رابط النجاح
     * @throws RuntimeException إذا فشل في إنشاء الجلسة
     */
    String createPaymentSession(Long userId, Long courseId);

    /**
     * تأكيد الدفع وتحديث حالة التسجيل
     * @param userId معرف المستخدم
     * @param courseId معرف الكورس
     * @return true إذا تم التأكيد بنجاح
     */
    boolean confirmPayment(Long userId, Long courseId);

    /**
     * التحقق من حالة الدفع
     * @param userId معرف المستخدم
     * @param courseId معرف الكورس
     * @return حالة الدفع
     */
    PaymentServiceImpl.PaymentStatus getPaymentStatus(Long userId, Long courseId);
}