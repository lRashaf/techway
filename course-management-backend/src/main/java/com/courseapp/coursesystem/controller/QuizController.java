package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.entity.*;
import com.courseapp.coursesystem.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*")
public class QuizController {

    @Autowired
    private QuizService quizService;

    // جلب كويز الكورس
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourseQuiz(@PathVariable Long courseId) {
        try {
            Optional<Quiz> quiz = quizService.getCourseQuiz(courseId);
            if (quiz.isPresent()) {
                return ResponseEntity.ok(quiz.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // إنشاء كويز للكورس (Admin only)
    @PostMapping("/course/{courseId}")
    public ResponseEntity<?> createCourseQuiz(@PathVariable Long courseId, @RequestBody Quiz quiz) {
        try {
            Quiz createdQuiz = quizService.createCourseQuiz(courseId, quiz);
            return ResponseEntity.ok(createdQuiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // تحديث كويز (Admin only)
    @PutMapping("/{quizId}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long quizId, @RequestBody Quiz quiz) {
        try {
            Quiz updatedQuiz = quizService.updateQuiz(quizId, quiz);
            return ResponseEntity.ok(updatedQuiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // بدء محاولة جديدة
    @PostMapping("/{quizId}/start")
    public ResponseEntity<?> startQuizAttempt(@PathVariable Long quizId, @RequestParam Long userId) {
        try {
            if (!quizService.canUserAccessQuiz(userId, null)) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }

            QuizAttempt attempt = quizService.startQuizAttempt(userId, quizId);
            return ResponseEntity.ok(attempt);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // تسجيل إجابة
    @PostMapping("/attempt/{attemptId}/answer")
    public ResponseEntity<?> submitAnswer(
            @PathVariable Long attemptId,
            @RequestBody Map<String, Object> answerData
    ) {
        try {
            Long questionId = Long.valueOf(answerData.get("questionId").toString());
            Long selectedOptionId = Long.valueOf(answerData.get("selectedOptionId").toString());

            QuizAttempt attempt = quizService.submitAnswer(attemptId, questionId, selectedOptionId);
            return ResponseEntity.ok(Map.of("success", true, "attempt", attempt));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // إنهاء المحاولة
    @PostMapping("/attempt/{attemptId}/complete")
    public ResponseEntity<?> completeQuizAttempt(@PathVariable Long attemptId) {
        try {
            QuizAttempt completedAttempt = quizService.completeQuizAttempt(attemptId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "attempt", completedAttempt,
                    "passed", completedAttempt.getIsPassed(),
                    "score", completedAttempt.getScorePercentage(),
                    "certificateGenerated", completedAttempt.getIsPassed()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // جلب محاولات المستخدم
    @GetMapping("/{quizId}/attempts")
    public ResponseEntity<?> getUserAttempts(@PathVariable Long quizId, @RequestParam Long userId) {
        try {
            List<QuizAttempt> attempts = quizService.getUserQuizAttempts(userId, quizId);
            return ResponseEntity.ok(attempts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // جلب أفضل محاولة
    @GetMapping("/{quizId}/best-attempt")
    public ResponseEntity<?> getBestAttempt(@PathVariable Long quizId, @RequestParam Long userId) {
        try {
            Optional<QuizAttempt> bestAttempt = quizService.getUserBestAttempt(userId, quizId);
            if (bestAttempt.isPresent()) {
                return ResponseEntity.ok(bestAttempt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // إضافة سؤال للكويز (Admin only)
    @PostMapping("/{quizId}/questions")
    public ResponseEntity<?> addQuestion(@PathVariable Long quizId, @RequestBody Question question) {
        try {
            Question createdQuestion = quizService.addQuestionToQuiz(quizId, question);
            return ResponseEntity.ok(createdQuestion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // حذف كويز (Admin only)
    @DeleteMapping("/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId) {
        try {
            quizService.deleteQuiz(quizId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Quiz deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // إحصائيات الكويز (Admin only)
    @GetMapping("/{quizId}/statistics")
    public ResponseEntity<?> getQuizStatistics(@PathVariable Long quizId) {
        try {
            Object[] stats = quizService.getQuizStatistics(quizId);
            return ResponseEntity.ok(Map.of(
                    "totalAttempts", stats[0],
                    "averageScore", stats[1],
                    "highestScore", stats[2],
                    "lowestScore", stats[3]
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}