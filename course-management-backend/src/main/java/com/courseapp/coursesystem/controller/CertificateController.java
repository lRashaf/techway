package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.entity.Certificate;
import com.courseapp.coursesystem.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "*")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    // جلب شهادة المستخدم للكورس
    @GetMapping("/user/{userId}/course/{courseId}")
    public ResponseEntity<?> getUserCourseCertificate(@PathVariable Long userId, @PathVariable Long courseId) {
        try {
            Optional<Certificate> certificate = certificateService.getUserCourseCertificate(userId, courseId);
            if (certificate.isPresent()) {
                return ResponseEntity.ok(certificate.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // جلب جميع شهادات المستخدم
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserCertificates(@PathVariable Long userId) {
        try {
            List<Certificate> certificates = certificateService.getUserCertificates(userId);
            return ResponseEntity.ok(certificates);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // توليد شهادة يدوياً (Admin only)
    @PostMapping("/generate")
    public ResponseEntity<?> generateCertificate(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            Long courseId = Long.valueOf(request.get("courseId").toString());
            Double finalScore = Double.valueOf(request.get("finalScore").toString());
            Double quizScore = Double.valueOf(request.get("quizScore").toString());

            Certificate certificate = certificateService.generateCertificate(userId, courseId, finalScore, quizScore);
            return ResponseEntity.ok(certificate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // التحقق من صحة الشهادة
    @GetMapping("/verify/{certificateNumber}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String certificateNumber) {
        try {
            Optional<Certificate> certificate = certificateService.getCertificateByNumber(certificateNumber);
            if (certificate.isPresent()) {
                boolean isValid = certificateService.verifyCertificate(certificateNumber);
                return ResponseEntity.ok(Map.of(
                        "valid", isValid,
                        "certificate", certificate.get()
                ));
            } else {
                return ResponseEntity.ok(Map.of("valid", false, "message", "Certificate not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // تحميل PDF الشهادة
    @GetMapping("/{certificateId}/download")
    public ResponseEntity<?> downloadCertificatePDF(@PathVariable Long certificateId) {
        try {
            byte[] pdfContent = certificateService.generateCertificatePDF(certificateId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "certificate-" + certificateId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // عرض PDF الشهادة في المتصفح
    @GetMapping("/{certificateId}/view")
    public ResponseEntity<?> viewCertificatePDF(@PathVariable Long certificateId) {
        try {
            byte[] pdfContent = certificateService.generateCertificatePDF(certificateId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=certificate-" + certificateId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // إلغاء شهادة (Admin only)
    @PostMapping("/{certificateId}/revoke")
    public ResponseEntity<?> revokeCertificate(@PathVariable Long certificateId, @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            certificateService.revokeCertificate(certificateId, reason);
            return ResponseEntity.ok(Map.of("success", true, "message", "Certificate revoked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // إحصائيات الشهادات لكورس (Admin only)
    @GetMapping("/course/{courseId}/statistics")
    public ResponseEntity<?> getCourseStatistics(@PathVariable Long courseId) {
        try {
            Object[] stats = certificateService.getCourseStatistics(courseId);
            return ResponseEntity.ok(Map.of(
                    "totalCertificates", stats[0],
                    "averageScore", stats[1]
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}