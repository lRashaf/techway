package com.courseapp.coursesystem.repository;

import com.courseapp.coursesystem.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // جلب جميع محاولات المستخدم لكويز معين
    List<QuizAttempt> findByUserIdAndQuizIdOrderByAttemptNumberDesc(Long userId, Long quizId);

    // جلب آخر محاولة للمستخدم في كويز معين
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.quiz.id = :quizId ORDER BY qa.attemptNumber DESC LIMIT 1")
    Optional<QuizAttempt> findLatestAttemptByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // جلب أفضل نتيجة للمستخدم في كويز معين
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.quiz.id = :quizId ORDER BY qa.scorePercentage DESC LIMIT 1")
    Optional<QuizAttempt> findBestAttemptByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // عدد محاولات المستخدم لكويز معين
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.quiz.id = :quizId")
    long countByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // جلب المحاولة مع الإجابات
    @Query("SELECT qa FROM QuizAttempt qa LEFT JOIN FETCH qa.answers a LEFT JOIN FETCH a.question LEFT JOIN FETCH a.selectedOption WHERE qa.id = :attemptId")
    Optional<QuizAttempt> findByIdWithAnswers(@Param("attemptId") Long attemptId);

    // جلب المحاولات الناجحة فقط
    List<QuizAttempt> findByUserIdAndQuizIdAndIsPassedTrueOrderByScorePercentageDesc(Long userId, Long quizId);

    // حذف محاولات كويز معين
    void deleteByQuizId(Long quizId);

    // حذف محاولات مستخدم معين
    void deleteByUserId(Long userId);

    // جلب إحصائيات الكويز
    @Query("SELECT COUNT(qa), AVG(qa.scorePercentage), MAX(qa.scorePercentage), MIN(qa.scorePercentage) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId AND qa.isCompleted = true")
    Object[] getQuizStatistics(@Param("quizId") Long quizId);
}