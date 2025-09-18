package com.courseapp.coursesystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "modules",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_module_course_order", columnNames = {"course_id", "order_index"})
        }
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // لكل موديل كورس واحد
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    private Course course;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String summary;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 1;

    @Column(name = "is_free", nullable = false)
    private Boolean isFree = false; // أول وحدتين مجانية حسب منطقك

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // دروس هذا الموديول
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Lesson> lessons = new ArrayList<>();

    // إضافة جديدة: نوع الوحدة لدعم الكويز كوحدة أخيرة
    @Enumerated(EnumType.STRING)
    @Column(name = "module_type", length = 20)
    private ModuleType moduleType = ModuleType.REGULAR; // REGULAR, QUIZ

    public Module() { }

    public Module(Course course, String title, String summary, Integer orderIndex, Boolean isFree) {
        this.course = course;
        this.title = title;
        this.summary = summary;
        this.orderIndex = orderIndex;
        this.isFree = isFree;
    }

    // Constructor للوحدة الكويز
    public Module(Course course, String title, String summary, Integer orderIndex, Boolean isFree, ModuleType moduleType) {
        this.course = course;
        this.title = title;
        this.summary = summary;
        this.orderIndex = orderIndex;
        this.isFree = isFree;
        this.moduleType = moduleType;
    }

    // Helper method للتحقق من كون الوحدة كويز
    public boolean isQuizModule() {
        return this.moduleType == ModuleType.QUIZ;
    }

    // Helper method لإضافة درس
    public void addLesson(Lesson lesson) {
        lesson.setModule(this);
        lesson.setOrderIndex(this.lessons.size() + 1);
        this.lessons.add(lesson);
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public Boolean getIsFree() { return isFree; }
    public void setIsFree(Boolean free) { isFree = free; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Lesson> getLessons() { return lessons; }
    public void setLessons(List<Lesson> lessons) { this.lessons = lessons; }

    public ModuleType getModuleType() { return moduleType; }
    public void setModuleType(ModuleType moduleType) { this.moduleType = moduleType; }
}