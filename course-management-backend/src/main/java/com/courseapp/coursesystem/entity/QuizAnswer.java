package com.courseapp.coursesystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_answers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuizAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // المحاولة المرتبطة بهذه الإجابة
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    @JsonIgnore
    private QuizAttempt quizAttempt;

    // السؤال المُجاب عليه
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // الخيار المختار من المستخدم
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;

    // هل الإجابة صحيحة؟
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    // وقت الإجابة
    @Column(name = "answered_at")
    private LocalDateTime answeredAt = LocalDateTime.now();

    // Constructors
    public QuizAnswer() {}

    public QuizAnswer(QuizAttempt quizAttempt, Question question, QuestionOption selectedOption) {
        this.quizAttempt = quizAttempt;
        this.question = question;
        this.selectedOption = selectedOption;
        this.isCorrect = selectedOption != null && Boolean.TRUE.equals(selectedOption.getIsCorrect());
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public QuizAttempt getQuizAttempt() { return quizAttempt; }
    public void setQuizAttempt(QuizAttempt quizAttempt) { this.quizAttempt = quizAttempt; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public QuestionOption getSelectedOption() { return selectedOption; }
    public void setSelectedOption(QuestionOption selectedOption) { this.selectedOption = selectedOption; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }

    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }
}