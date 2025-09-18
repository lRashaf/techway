package com.courseapp.coursesystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "question_options")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // كل خيار مرتبط بسؤال
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionText;

    // هل هذا الخيار صحيح؟
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    // ترتيب الخيار
    @Column(name = "option_index", nullable = false)
    private Integer optionIndex = 1;

    // Constructors
    public QuestionOption() {}

    public QuestionOption(Question question, String optionText, Boolean isCorrect, Integer optionIndex) {
        this.question = question;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.optionIndex = optionIndex;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }

    public Integer getOptionIndex() { return optionIndex; }
    public void setOptionIndex(Integer optionIndex) { this.optionIndex = optionIndex; }
}