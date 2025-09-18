package com.courseapp.coursesystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(
        name = "lessons",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_lesson_module_order", columnNames = {"module_id", "order_index"})
        }
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // لكل درس موديول
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    @JsonIgnore
    private Module module;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex = 1;

    @Column(name = "duration_minutes")
    private Integer durationMinutes; // معلومات فقط للعرض

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", length = 20, nullable = false)
    private LessonContentType contentType = LessonContentType.VIDEO;

    // للفيديوهات (مبدئيًا URL مباشر)
    @Column(name = "video_url", length = 1000)
    private String videoUrl;

    // لعرض جزء قبل الدفع (اختياري)
    @Column(name = "is_preview", nullable = false)
    private Boolean isPreview = false;

    public Lesson() { }

    public Lesson(Module module, String title, Integer orderIndex, Integer durationMinutes, LessonContentType contentType, String videoUrl, Boolean isPreview) {
        this.module = module;
        this.title = title;
        this.orderIndex = orderIndex;
        this.durationMinutes = durationMinutes;
        this.contentType = contentType;
        this.videoUrl = videoUrl;
        this.isPreview = isPreview != null ? isPreview : false;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public Module getModule() { return module; }
    public void setModule(Module module) { this.module = module; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public LessonContentType getContentType() { return contentType; }
    public void setContentType(LessonContentType contentType) { this.contentType = contentType; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public Boolean getIsPreview() { return isPreview; }
    public void setIsPreview(Boolean preview) { isPreview = preview; }
}
