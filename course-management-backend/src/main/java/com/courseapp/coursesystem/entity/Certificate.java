package com.courseapp.coursesystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificates",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // المستخدم الحاصل على الشهادة
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // الكورس المُكتمل
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    // رقم الشهادة الفريد
    @Column(name = "certificate_number", nullable = false, unique = true, length = 50)
    private String certificateNumber;

    // تاريخ الإكمال
    @Column(name = "completion_date", nullable = false)
    private LocalDateTime completionDate = LocalDateTime.now();

    // تاريخ إصدار الشهادة
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt = LocalDateTime.now();

    // النسبة النهائية للكورس (للعرض)
    @Column(name = "final_score")
    private Double finalScore;

    // نسبة اجتياز الكويز
    @Column(name = "quiz_score")
    private Double quizScore;

    // حالة الشهادة
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CertificateStatus status = CertificateStatus.ACTIVE;

    // رابط ملف PDF (اختياري - يمكن تخزينه أو توليده حسب الطلب)
    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    // معلومات إضافية (JSON format)
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    // Constructors
    public Certificate() {}

    public Certificate(User user, Course course, String certificateNumber, Double finalScore, Double quizScore) {
        this.user = user;
        this.course = course;
        this.certificateNumber = certificateNumber;
        this.finalScore = finalScore;
        this.quizScore = quizScore;
    }

    // Helper method لتوليد رقم شهادة فريد
    public static String generateCertificateNumber(Long userId, Long courseId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return String.format("CERT-%d-%d-%s", courseId, userId, timestamp.substring(timestamp.length() - 6));
    }

    // Helper method للتحقق من صلاحية الشهادة
    public boolean isValid() {
        return this.status == CertificateStatus.ACTIVE;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }

    public LocalDateTime getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public Double getFinalScore() { return finalScore; }
    public void setFinalScore(Double finalScore) { this.finalScore = finalScore; }

    public Double getQuizScore() { return quizScore; }
    public void setQuizScore(Double quizScore) { this.quizScore = quizScore; }

    public CertificateStatus getStatus() { return status; }
    public void setStatus(CertificateStatus status) { this.status = status; }

    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}

