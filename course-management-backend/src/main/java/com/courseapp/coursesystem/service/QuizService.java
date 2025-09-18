package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.*;
import com.courseapp.coursesystem.exception.ValidationException;
import com.courseapp.coursesystem.exception.CourseNotFoundException;
import com.courseapp.coursesystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CertificateService certificateService;

    // جلب كويز الكورس مع الأسئلة والخيارات (إصلاح MultipleBagFetch)
    public Optional<Quiz> getCourseQuiz(Long courseId) {
        Optional<Quiz> quizOpt = quizRepository.findByCourseIdWithQuestions(courseId);

        if (quizOpt.isPresent()) {
            Quiz quiz = quizOpt.get();

            // تحميل الخيارات للأسئلة بشكل منفصل (حل مشكلة MultipleBag)
            quiz.getQuestions().forEach(question -> {
                // تحفيز تحميل الخيارات (Hibernate سيجلبها تلقائياً)
                question.getOptions().size();
            });

            return Optional.of(quiz);
        }

        return Optional.empty();
    }

    // إنشاء كويز جديد للكورس
    public Quiz createCourseQuiz(Long courseId, Quiz quiz) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        // التأكد من عدم وجود كويز للكورس مسبقاً
        if (quizRepository.findByCourseId(courseId).isPresent()) {
            throw new ValidationException("Course already has a quiz");
        }

        quiz.setCourse(course);
        quiz.setCreatedAt(LocalDateTime.now());

        return quizRepository.save(quiz);
    }

    // تحديث كويز
    public Quiz updateQuiz(Long quizId, Quiz updatedQuiz) {
        Quiz existingQuiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ValidationException("Quiz not found"));

        existingQuiz.setTitle(updatedQuiz.getTitle());
        existingQuiz.setDescription(updatedQuiz.getDescription());
        existingQuiz.setPassingScore(updatedQuiz.getPassingScore());
        existingQuiz.setTimeLimitMinutes(updatedQuiz.getTimeLimitMinutes());
        existingQuiz.setMaxAttempts(updatedQuiz.getMaxAttempts());
        existingQuiz.setShuffleQuestions(updatedQuiz.getShuffleQuestions());
        existingQuiz.setUpdatedAt(LocalDateTime.now());

        return quizRepository.save(existingQuiz);
    }

    // بدء محاولة جديدة للكويز
    public QuizAttempt startQuizAttempt(Long userId, Long quizId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("User not found"));

        // استخدام الطريقة الآمنة لجلب الكويز
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new ValidationException("Quiz not found"));

        // تحميل الخيارات للأسئلة
        quiz.getQuestions().forEach(question -> question.getOptions().size());

        // التحقق من عدد المحاولات المسموحة
        long currentAttempts = quizAttemptRepository.countByUserAndQuiz(userId, quizId);
        if (quiz.getMaxAttempts() != null && currentAttempts >= quiz.getMaxAttempts()) {
            throw new ValidationException("Maximum attempts exceeded");
        }

        // إنشاء محاولة جديدة
        QuizAttempt attempt = new QuizAttempt(user, quiz, (int) (currentAttempts + 1));
        return quizAttemptRepository.save(attempt);
    }

    // تسجيل إجابة في المحاولة
    public QuizAttempt submitAnswer(Long attemptId, Long questionId, Long selectedOptionId) {
        QuizAttempt attempt = quizAttemptRepository.findByIdWithAnswers(attemptId)
                .orElseThrow(() -> new ValidationException("Quiz attempt not found"));

        if (attempt.getIsCompleted()) {
            throw new ValidationException("Quiz attempt is already completed");
        }

        // البحث عن السؤال والخيار
        Question question = attempt.getQuiz().getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Question not found in this quiz"));

        // تأكد من تحميل الخيارات
        question.getOptions().size();

        QuestionOption selectedOption = question.getOptions().stream()
                .filter(opt -> opt.getId().equals(selectedOptionId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Option not found for this question"));

        // إضافة الإجابة
        attempt.addAnswer(question, selectedOption);

        return quizAttemptRepository.save(attempt);
    }

    // إنهاء المحاولة وحساب النتيجة
    public QuizAttempt completeQuizAttempt(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findByIdWithAnswers(attemptId)
                .orElseThrow(() -> new ValidationException("Quiz attempt not found"));

        if (attempt.getIsCompleted()) {
            throw new ValidationException("Quiz attempt is already completed");
        }

        // إنهاء المحاولة وحساب النتيجة
        attempt.complete();
        QuizAttempt completedAttempt = quizAttemptRepository.save(attempt);

        // إذا نجح في الكويز، إنشاء شهادة
        if (completedAttempt.getIsPassed()) {
            try {
                certificateService.generateCertificate(
                        attempt.getUser().getId(),
                        attempt.getQuiz().getCourse().getId(),
                        100.0, // تقدم الكورس الكامل
                        completedAttempt.getScorePercentage()
                );
            } catch (Exception e) {
                // لا نريد إيقاف العملية إذا فشلت الشهادة
                System.err.println("Failed to generate certificate: " + e.getMessage());
            }
        }

        return completedAttempt;
    }

    // جلب محاولات المستخدم
    public List<QuizAttempt> getUserQuizAttempts(Long userId, Long quizId) {
        return quizAttemptRepository.findByUserIdAndQuizIdOrderByAttemptNumberDesc(userId, quizId);
    }

    // جلب أفضل محاولة للمستخدم
    public Optional<QuizAttempt> getUserBestAttempt(Long userId, Long quizId) {
        return quizAttemptRepository.findBestAttemptByUserAndQuiz(userId, quizId);
    }

    // التحقق من إمكانية الوصول للكويز
    public boolean canUserAccessQuiz(Long userId, Long courseId) {
        // للبساطة الآن، نقول أي مستخدم مسجل يمكنه الوصول
        return true;
    }

    // حذف كويز (للأدمن فقط)
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ValidationException("Quiz not found"));

        // حذف المحاولات أولاً
        quizAttemptRepository.deleteByQuizId(quizId);

        // حذف الكويز
        quizRepository.delete(quiz);
    }

    // إضافة سؤال للكويز
    public Question addQuestionToQuiz(Long quizId, Question question) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ValidationException("Quiz not found"));

        question.setQuiz(quiz);
        question.setOrderIndex(quiz.getQuestions().size() + 1);
        quiz.getQuestions().add(question);

        Quiz savedQuiz = quizRepository.save(quiz);

        // إرجاع السؤال المحفوظ
        return savedQuiz.getQuestions().get(savedQuiz.getQuestions().size() - 1);
    }

    // جلب إحصائيات الكويز
    public Object[] getQuizStatistics(Long quizId) {
        return quizAttemptRepository.getQuizStatistics(quizId);
    }
}