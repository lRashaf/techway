package com.courseapp.coursesystem.dto;

import java.time.LocalDateTime;

public class ActivityItemDTO {
    private String type; // ENROLL | COURSE_CREATE
    private String user; // اسم المستخدم (إن وجد)
    private String course; // اسم الكورس
    private LocalDateTime at; // وقت الحدث
    private String by; // من نفّذ (للإنشاء مثلاً)

    public ActivityItemDTO() {}

    public ActivityItemDTO(String type, String user, String course, LocalDateTime at, String by) {
        this.type = type;
        this.user = user;
        this.course = course;
        this.at = at;
        this.by = by;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public LocalDateTime getAt() { return at; }
    public void setAt(LocalDateTime at) { this.at = at; }

    public String getBy() { return by; }
    public void setBy(String by) { this.by = by; }
}
