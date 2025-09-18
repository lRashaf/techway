package com.courseapp.coursesystem.dto;

import com.courseapp.coursesystem.entity.LocationType;

import java.util.ArrayList;
import java.util.List;

public class CourseOutlineDto {
    private Long courseId;
    private String courseTitle;
    private Boolean isFreeCourse;
    private LocationType locationType;
    private Double overallProgressPercent; // لاحقًا مع LessonProgress
    private List<ModuleDto> modules = new ArrayList<>();

    public CourseOutlineDto() { }

    public CourseOutlineDto(Long courseId, String courseTitle, Boolean isFreeCourse, LocationType locationType, Double overallProgressPercent) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.isFreeCourse = isFreeCourse;
        this.locationType = locationType;
        this.overallProgressPercent = overallProgressPercent;
    }

    // Getters/Setters
    public Long getCourseId() { return courseId; }
    public String getCourseTitle() { return courseTitle; }
    public Boolean getIsFreeCourse() { return isFreeCourse; }
    public LocationType getLocationType() { return locationType; }
    public Double getOverallProgressPercent() { return overallProgressPercent; }
    public List<ModuleDto> getModules() { return modules; }

    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public void setIsFreeCourse(Boolean isFreeCourse) { this.isFreeCourse = isFreeCourse; }
    public void setLocationType(LocationType locationType) { this.locationType = locationType; }
    public void setOverallProgressPercent(Double overallProgressPercent) { this.overallProgressPercent = overallProgressPercent; }
    public void setModules(List<ModuleDto> modules) { this.modules = modules; }
}
