package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.*;
import com.courseapp.coursesystem.exception.ValidationException;
import com.courseapp.coursesystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

// PDFBox 3.x
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

@Service
@Transactional
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    // توليد شهادة تلقائياً
    public Certificate generateCertificate(Long userId, Long courseId, Double finalScore, Double quizScore) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ValidationException("Course not found"));

        if (certificateRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new ValidationException("Certificate already exists for this user and course");
        }

        String certificateNumber = Certificate.generateCertificateNumber(userId, courseId);

        Certificate certificate = new Certificate(user, course, certificateNumber, finalScore, quizScore);
        certificate.setCompletionDate(LocalDateTime.now());
        certificate.setIssuedAt(LocalDateTime.now());

        return certificateRepository.save(certificate);
    }

    // جلب شهادة المستخدم للكورس
    public Optional<Certificate> getUserCourseCertificate(Long userId, Long courseId) {
        return certificateRepository.findByUserIdAndCourseId(userId, courseId);
    }

    // جلب شهادات المستخدم
    public List<Certificate> getUserCertificates(Long userId) {
        return certificateRepository.findByUserIdOrderByIssuedAtDesc(userId);
    }

    // جلب شهادة برقمها
    public Optional<Certificate> getCertificateByNumber(String certificateNumber) {
        return certificateRepository.findByCertificateNumber(certificateNumber);
    }

    // التحقق من صحة الشهادة
    public boolean verifyCertificate(String certificateNumber) {
        Optional<Certificate> certificate = certificateRepository.findByCertificateNumber(certificateNumber);
        return certificate.isPresent() && certificate.get().isValid();
    }

    // إلغاء شهادة
    public void revokeCertificate(Long certificateId, String reason) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ValidationException("Certificate not found"));
        certificate.setStatus(CertificateStatus.REVOKED);
        certificate.setMetadata("Revoked: " + reason + " at " + LocalDateTime.now());
        certificateRepository.save(certificate);
    }

    // توليد PDF للشهادة - تنسيق أفقي مع إطار مزخرف وشعار اختياري بالمنتصف
    public byte[] generateCertificatePDF(Long certificateId) {
        Certificate c = certificateRepository.findByIdWithUserAndCourse(certificateId)
                .orElseThrow(() -> new ValidationException("Certificate not found"));
        if (!c.isValid()) throw new ValidationException("Certificate is not valid");

        // قيم آمنة ضد null
        String studentName = (c.getUser()!=null && c.getUser().getName()!=null) ? c.getUser().getName() : "Student Name";
        String courseTitle = (c.getCourse()!=null && c.getCourse().getTitle()!=null) ? c.getCourse().getTitle() : "Course Title";
        double finalScore  = (c.getFinalScore()!=null) ? c.getFinalScore() : 0.0;
        double quizScore   = (c.getQuizScore()!=null)  ? c.getQuizScore()  : 0.0;
        String certNum     = (c.getCertificateNumber()!=null) ? c.getCertificateNumber() : String.valueOf(c.getId());
        String dateStr     = (c.getCompletionDate()!=null ? c.getCompletionDate()
                : (c.getIssuedAt()!=null ? c.getIssuedAt() : LocalDateTime.now()))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // تحميل خط عربي يدعم يونيكود
            PDType0Font font;
            try (InputStream is = getClass().getResourceAsStream("/fonts/NotoNaskhArabic-VariableFont_wght.ttf")) {
                if (is == null) throw new IllegalStateException("Font file not found at /fonts/NotoNaskhArabic-VariableFont_wght.ttf");
                font = PDType0Font.load(doc, is, true); // embed
            }

            // A4 Landscape
            PDRectangle a4 = PDRectangle.A4;
            PDRectangle landscape = new PDRectangle(a4.getHeight(), a4.getWidth());
            PDPage page = new PDPage(landscape);
            doc.addPage(page);

            float W = page.getMediaBox().getWidth();
            float H = page.getMediaBox().getHeight();
            float margin = 36f;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                // خلفية
                cs.setNonStrokingColor(new Color(253, 253, 255));
                cs.addRect(0, 0, W, H); cs.fill();

                // إطارات زخرفية (3 مستويات)
                cs.setLineWidth(3f);
                cs.setStrokingColor(new Color(37, 99, 235)); // أزرق داكن
                cs.addRect(margin, margin, W - 2*margin, H - 2*margin); cs.stroke();

                cs.setLineWidth(1.5f);
                cs.setStrokingColor(new Color(59, 130, 246));
                float m2 = margin + 12f;
                cs.addRect(m2, m2, W - 2*m2, H - 2*m2); cs.stroke();

                cs.setLineWidth(0.8f);
                cs.setStrokingColor(new Color(147, 197, 253));
                float m3 = m2 + 10f;
                cs.addRect(m3, m3, W - 2*m3, H - 2*m3); cs.stroke();

                // لمسات في الزوايا
                cs.setLineWidth(1f);
                cs.setStrokingColor(new Color(59, 130, 246));
                float t = 18f;
                // TL
                cs.moveTo(m3, H-m3); cs.lineTo(m3+t, H-m3); cs.stroke();
                cs.moveTo(m3, H-m3); cs.lineTo(m3, H-m3-t); cs.stroke();
                // TR
                cs.moveTo(W-m3, H-m3); cs.lineTo(W-m3-t, H-m3); cs.stroke();
                cs.moveTo(W-m3, H-m3); cs.lineTo(W-m3, H-m3-t); cs.stroke();
                // BL
                cs.moveTo(m3, m3); cs.lineTo(m3+t, m3); cs.stroke();
                cs.moveTo(m3, m3); cs.lineTo(m3, m3+t); cs.stroke();
                // BR
                cs.moveTo(W-m3, m3); cs.lineTo(W-m3-t, m3); cs.stroke();
                cs.moveTo(W-m3, m3); cs.lineTo(W-m3, m3+t); cs.stroke();

                // عنوان كبير
                float y = H - m3 - 60f;
                drawCenteredText(cs, font, 36f, W, y, "CERTIFICATE", new Color(30, 58, 138));
                y -= 24f;

                // سطر فرعي
                drawCenteredText(cs, font, 14f, W, y, "This certificate is presented to", new Color(71, 85, 105));
                y -= 34f;

                // اسم الطالب كبير وبالوسط
                drawCenteredText(cs, font, 36f, W, y, studentName, new Color(17, 24, 39));
                y -= 22f;

                // فاصل رفيع
                cs.setStrokingColor(new Color(203, 213, 225));
                cs.setLineWidth(0.8f);
                cs.moveTo(W * 0.32f, y); cs.lineTo(W * 0.68f, y); cs.stroke();
                y -= 28f;

                // وصف الكورس
                drawCenteredText(cs, font, 13f, W, y,
                        "Successfully completed the course: " + courseTitle,
                        new Color(51, 65, 85));
                y -= 22f;

                // شعار اختياري في الوسط
                try (InputStream logoIs = getClass().getResourceAsStream("/images/logo.png")) {
                    if (logoIs != null) {
                        PDImageXObject logo = PDImageXObject.createFromByteArray(doc, logoIs.readAllBytes(), "logo");
                        float logoW = 80f;
                        float logoH = logo.getHeight() * (logoW / logo.getWidth());
                        float logoX = (W - logoW) / 2f;
                        float logoY = y - logoH - 6f;
                        cs.drawImage(logo, logoX, logoY, logoW, logoH);
                        y = logoY - 24f;
                    }
                } catch (Exception ignore) { /* لا يوجد شعار */ }

                // صف التفاصيل السفلي (تاريخ - الدرجات - رقم الشهادة)
                float baseY = m3 + 88f;
                float colX1 = m3 + 30f;
                float colX2 = W/2f - 80f;
                float colX3 = W - m3 - 260f;

                showText(cs, font, 12f, colX1, baseY+22f, "DATE", new Color(100,116,139));
                showText(cs, font, 12f, colX1, baseY, dateStr, new Color(17,24,39));

                showText(cs, font, 12f, colX2, baseY+22f, "SCORES", new Color(100,116,139));
                showText(cs, font, 12f, colX2, baseY,
                        String.format("Final %.1f%% — Quiz %.1f%%", finalScore, quizScore),
                        new Color(17,24,39));

                showText(cs, font, 12f, colX3, baseY+22f, "CERTIFICATE NO.", new Color(100,116,139));
                showText(cs, font, 12f, colX3, baseY, certNum, new Color(17,24,39));

                // خط توقيع يمين
                float sigY = m3 + 40f;
                float sigRight = W - m3 - 40f;
                cs.setStrokingColor(new Color(203,213,225));
                cs.setLineWidth(1f);
                cs.moveTo(sigRight - 160f, sigY); cs.lineTo(sigRight, sigY); cs.stroke();
                showText(cs, font, 11f, sigRight - 150f, sigY - 14f, "SIGNATURE", new Color(100,116,139));

                // شريط زخرفي سفلي
                cs.setNonStrokingColor(new Color(59,130,246));
                cs.addRect(m3, m3 + 8f, W - 2*m3, 2.2f); cs.fill();
            }

            doc.save(out);
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("PDF generation failed (landscape styled)", e);
        }
    }

    // ===== Helpers =====
    private void showText(PDPageContentStream cs, PDType0Font font, float size,
                          float x, float y, String text, Color color) throws Exception {
        cs.setNonStrokingColor(color);
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private void drawCenteredText(PDPageContentStream cs, PDType0Font font, float size,
                                  float pageWidth, float y, String text, Color color) throws Exception {
        float textW = font.getStringWidth(text) / 1000f * size;
        float x = (pageWidth - textW) / 2f;
        showText(cs, font, size, x, y, text, color);
    }

    // إحصائيات الشهادات لكورس
    public Object[] getCourseStatistics(Long courseId) {
        return certificateRepository.getCourseStatistics(courseId);
    }

    // حذف شهادات كورس (عند حذف الكورس)
    public void deleteCertificatesByCourse(Long courseId) {
        certificateRepository.deleteByCourseId(courseId);
    }
}
