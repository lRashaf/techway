package com.courseapp.coursesystem.dto;

import com.courseapp.coursesystem.entity.LessonContentType;

public class LessonDto {
    private Long id;
    private String title;
    private Integer orderIndex;
    private Integer durationMinutes;
    private LessonContentType contentType;
    private Boolean isLocked;
    private Boolean isPreview;
    private String videoUrl; // إضافة جديدة

    public LessonDto() { }

    public LessonDto(Long id, String title, Integer orderIndex, Integer durationMinutes,
                     LessonContentType contentType, Boolean isLocked, Boolean isPreview) {
        this.id = id;
        this.title = title;
        this.orderIndex = orderIndex;
        this.durationMinutes = durationMinutes;
        this.contentType = contentType;
        this.isLocked = isLocked;
        this.isPreview = isPreview;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Integer getOrderIndex() { return orderIndex; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public LessonContentType getContentType() { return contentType; }
    public Boolean getIsLocked() { return isLocked; }
    public Boolean getIsPreview() { return isPreview; }
    public String getVideoUrl() { return videoUrl; } // جديد

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setContentType(LessonContentType contentType) { this.contentType = contentType; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }
    public void setIsPreview(Boolean isPreview) { this.isPreview = isPreview; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; } // جديد
}