package com.courseapp.coursesystem.dto;

import java.time.LocalDateTime;

public class RecentCourseDTO {
    private Long id;
    private String title;
    private String instructor;
    private boolean isFree;
    private LocalDateTime createdAt;

    public RecentCourseDTO() {}

    public RecentCourseDTO(Long id, String title, String instructor, boolean isFree, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.instructor = instructor;
        this.isFree = isFree;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public boolean isFree() { return isFree; }
    public void setFree(boolean free) { isFree = free; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
