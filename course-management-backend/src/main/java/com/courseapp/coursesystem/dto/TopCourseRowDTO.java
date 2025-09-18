package com.courseapp.coursesystem.dto;

import java.math.BigDecimal;

public class TopCourseRowDTO {
    private Long courseId;
    private String title;
    private String category;
    private long students;
    private BigDecimal revenue;
    private String status;
    private Double rating;

    public TopCourseRowDTO() {}

    public TopCourseRowDTO(Long courseId, String title, String category, long students,
                           BigDecimal revenue, String status, Double rating) {
        this.courseId = courseId;
        this.title = title;
        this.category = category;
        this.students = students;
        this.revenue = revenue;
        this.status = status;
        this.rating = rating;
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public long getStudents() { return students; }
    public void setStudents(long students) { this.students = students; }

    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
}
