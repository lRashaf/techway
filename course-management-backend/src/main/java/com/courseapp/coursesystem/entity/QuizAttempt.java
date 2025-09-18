package com.courseapp.coursesystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // المستخدم الذي يحل الكويز
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // الكويز المحلول
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;

    // رقم المحاولة (1، 2، 3...)
    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber = 1;

    // النتيجة النهائية (درجات صحيحة / إجمالي الدرجات * 100)
    @Column(name = "score_percentage")
    private Double scorePercentage;

    // عدد الإجابات الصحيحة
    @Column(name = "correct_answers", nullable = false)
    private Integer correctAnswers = 0;

    // إجمالي عدد الأسئلة
    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions = 0;

    // هل نجح في الكويز؟
    @Column(name = "is_passed")
    private Boolean isPassed = false;

    // وقت بدء الكويز
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    // وقت انتهاء الكويز
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // هل المحاولة مكتملة؟
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false;

    // الوقت المستغرق بالدقائق
    @Column(name = "time_taken_minutes")
    private Integer timeTakenMinutes;

    // إجابات المستخدم
    @OneToMany(mappedBy = "quizAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAnswer> answers = new ArrayList<>();

    // Constructors
    public QuizAttempt() {}

    public QuizAttempt(User user, Quiz quiz, Integer attemptNumber) {
        this.user = user;
        this.quiz = quiz;
        this.attemptNumber = attemptNumber;
        this.totalQuestions = quiz.getQuestions().size();
    }

    // Helper method لإضافة إجابة
    public void addAnswer(Question question, QuestionOption selectedOption) {
        QuizAnswer answer = new QuizAnswer();
        answer.setQuizAttempt(this);
        answer.setQuestion(question);
        answer.setSelectedOption(selectedOption);
        answer.setIsCorrect(selectedOption != null && Boolean.TRUE.equals(selectedOption.getIsCorrect()));
        this.answers.add(answer);
    }

    // Helper method لحساب النتيجة
    public void calculateScore() {
        this.correctAnswers = (int) answers.stream()
                .mapToInt(answer -> Boolean.TRUE.equals(answer.getIsCorrect()) ? 1 : 0)
                .sum();

        if (this.totalQuestions > 0) {
            this.scorePercentage = (double) this.correctAnswers / this.totalQuestions * 100.0;
            this.isPassed = this.scorePercentage >= this.quiz.getPassingScore();
        }
    }

    // Helper method لإنهاء المحاولة
    public void complete() {
        this.completedAt = LocalDateTime.now();
        this.isCompleted = true;

        // حساب الوقت المستغرق
        if (this.startedAt != null) {
            long minutes = java.time.Duration.between(this.startedAt, this.completedAt).toMinutes();
            this.timeTakenMinutes = (int) minutes;
        }

        calculateScore();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public Integer getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(Integer attemptNumber) { this.attemptNumber = attemptNumber; }

    public Double getScorePercentage() { return scorePercentage; }
    public void setScorePercentage(Double scorePercentage) { this.scorePercentage = scorePercentage; }

    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Boolean getIsPassed() { return isPassed; }
    public void setIsPassed(Boolean isPassed) { this.isPassed = isPassed; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }

    public Integer getTimeTakenMinutes() { return timeTakenMinutes; }
    public void setTimeTakenMinutes(Integer timeTakenMinutes) { this.timeTakenMinutes = timeTakenMinutes; }

    public List<QuizAnswer> getAnswers() { return answers; }
    public void setAnswers(List<QuizAnswer> answers) { this.answers = answers; }
}