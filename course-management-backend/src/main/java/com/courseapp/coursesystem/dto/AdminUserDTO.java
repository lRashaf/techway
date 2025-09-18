package com.courseapp.coursesystem.dto;

import com.courseapp.coursesystem.entity.User;
import java.time.LocalDateTime;

public class AdminUserDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String status;
    private LocalDateTime createdAt;

    public AdminUserDTO() {}

    public AdminUserDTO(Long id, String name, String email, String role, String status, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static AdminUserDTO from(User u) {
        return new AdminUserDTO(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getRole(),
                u.getStatus(),
                u.getCreatedAt()
        );
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
