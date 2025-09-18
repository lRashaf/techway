package com.courseapp.coursesystem.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminOverviewDTO {
    private long totalUsers;
    private long totalCourses;
    private long freeCourses;
    private long paidCourses;
    private long newEnrollments30d;
    private BigDecimal totalRevenue;
    private List<RecentCourseDTO> recentCourses;

    public AdminOverviewDTO() {}

    public AdminOverviewDTO(long totalUsers, long totalCourses, long freeCourses, long paidCourses,
                            long newEnrollments30d, BigDecimal totalRevenue,
                            List<RecentCourseDTO> recentCourses) {
        this.totalUsers = totalUsers;
        this.totalCourses = totalCourses;
        this.freeCourses = freeCourses;
        this.paidCourses = paidCourses;
        this.newEnrollments30d = newEnrollments30d;
        this.totalRevenue = totalRevenue;
        this.recentCourses = recentCourses;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalCourses() { return totalCourses; }
    public void setTotalCourses(long totalCourses) { this.totalCourses = totalCourses; }

    public long getFreeCourses() { return freeCourses; }
    public void setFreeCourses(long freeCourses) { this.freeCourses = freeCourses; }

    public long getPaidCourses() { return paidCourses; }
    public void setPaidCourses(long paidCourses) { this.paidCourses = paidCourses; }

    public long getNewEnrollments30d() { return newEnrollments30d; }
    public void setNewEnrollments30d(long newEnrollments30d) { this.newEnrollments30d = newEnrollments30d; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public List<RecentCourseDTO> getRecentCourses() { return recentCourses; }
    public void setRecentCourses(List<RecentCourseDTO> recentCourses) { this.recentCourses = recentCourses; }
}
