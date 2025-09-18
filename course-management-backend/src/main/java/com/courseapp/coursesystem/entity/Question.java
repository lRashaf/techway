package com.courseapp.coursesystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // كل سؤال مرتبط بكويز
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    // نوع السؤال
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

    // ترتيب السؤال في الكويز
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 1;

    // درجة السؤال (افتراضي 1 درجة)
    @Column(name = "points", nullable = false)
    private Integer points = 1;

    // شرح الإجابة (اختياري)
    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // خيارات الإجابة
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("optionIndex ASC")
    private List<QuestionOption> options = new ArrayList<>();

    // Constructors
    public Question() {}

    public Question(Quiz quiz, String questionText, QuestionType questionType, Integer orderIndex) {
        this.quiz = quiz;
        this.questionText = questionText;
        this.questionType = questionType;
        this.orderIndex = orderIndex;
    }

    // Helper method لإضافة خيار إجابة
    public QuestionOption addOption(String text, Boolean isCorrect) {
        QuestionOption option = new QuestionOption();
        option.setQuestion(this);
        option.setOptionText(text);
        option.setIsCorrect(isCorrect != null ? isCorrect : false);
        option.setOptionIndex(this.options.size() + 1);
        this.options.add(option);
        return option;
    }

    // Helper method للحصول على الإجابة الصحيحة
    public QuestionOption getCorrectOption() {
        return this.options.stream()
                .filter(opt -> Boolean.TRUE.equals(opt.getIsCorrect()))
                .findFirst()
                .orElse(null);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<QuestionOption> getOptions() { return options; }
    public void setOptions(List<QuestionOption> options) { this.options = options; }
}

