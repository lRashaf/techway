package com.courseapp.coursesystem.dto;

import java.util.ArrayList;
import java.util.List;

public class ModuleDto {
    private Long id;
    private String title;
    private String summary;
    private Integer orderIndex;
    private Boolean isFree;
    private Double progressPercent; // لاحقًا عند إضافة التقدم
    private List<LessonDto> lessons = new ArrayList<>();

    public ModuleDto() { }

    public ModuleDto(Long id, String title, String summary, Integer orderIndex, Boolean isFree, Double progressPercent) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.orderIndex = orderIndex;
        this.isFree = isFree;
        this.progressPercent = progressPercent;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public Integer getOrderIndex() { return orderIndex; }
    public Boolean getIsFree() { return isFree; }
    public Double getProgressPercent() { return progressPercent; }
    public List<LessonDto> getLessons() { return lessons; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    public void setIsFree(Boolean free) { isFree = free; }
    public void setProgressPercent(Double progressPercent) { this.progressPercent = progressPercent; }
    public void setLessons(List<LessonDto> lessons) { this.lessons = lessons; }
}
