package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.exception.ApiResponse;
import com.courseapp.coursesystem.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 1) إنشاء جلسة دفع
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createPayment(
            @RequestParam Long userId,
            @RequestParam Long courseId) {

        try {
            // تحقق من وجود المعاملات المطلوبة
            if (userId == null || courseId == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>("User ID and Course ID are required"));
            }

            String url = paymentService.createPaymentSession(userId, courseId);
            return ResponseEntity.ok(new ApiResponse<>(url, "Payment session created"));

        } catch (Exception e) {
            e.printStackTrace(); // للتشخيص
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Error creating payment: " + e.getMessage()));
        }
    }

    // 2) نجاح الدفع -> Redirect إلى الفرونت
    @GetMapping("/success")
    public ResponseEntity<Void> confirmPayment(
            @RequestParam Long userId,
            @RequestParam Long courseId) {
        try {
            boolean ok = paymentService.confirmPayment(userId, courseId);
            // رجّع المستخدم لواجهة الفرونت مع فلاغ يوضح النتيجة
            URI go = URI.create("http://localhost:3000/courses/" + courseId + (ok ? "?paid=1" : "?paid=0"));
            return ResponseEntity.status(302).location(go).build(); // 302 Found
        } catch (Exception e) {
            e.printStackTrace();
            URI errorUri = URI.create("http://localhost:3000/courses/" + courseId + "?paid=0&error=1");
            return ResponseEntity.status(302).location(errorUri).build();
        }
    }
}