    package com.courseapp.coursesystem.entity;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import jakarta.persistence.*;

    import java.time.LocalDateTime;
    import java.util.HashSet;
    import java.util.Set;

    @Entity
    @Table(name = "users")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 100)
        private String name;

        @Column(nullable = false, unique = true, length = 100)
        private String email;

        @Column(nullable = false)
        private String password;

        @Column(length = 20)
        private String role = "USER";

        @Column(name = "created_at")
        private LocalDateTime createdAt = LocalDateTime.now();

        // Relationships
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private Set<Enrollment> enrollments = new HashSet<>();

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private Set<Review> reviews = new HashSet<>();

        @Column(nullable = false)
        private String status = "ACTIVE";

        // Default constructor
        public User() {}

        // Constructor
        public User(String name, String email, String password,String status) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.status=status;
        }


        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public Set<Enrollment> getEnrollments() { return enrollments; }
        public void setEnrollments(Set<Enrollment> enrollments) { this.enrollments = enrollments; }

        public Set<Review> getReviews() { return reviews; }
        public void setReviews(Set<Review> reviews) { this.reviews = reviews; }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }