package com.courseapp.coursesystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String location;

    private Integer duration; // in hours

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "is_free")
    private Boolean isFree = false;

    @Column(length = 100)
    private String instructor;

    // ✨ رابط الصورة
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // ====== حقول الخريطة (الموقع الحضوري) ======
    // نوع الموقع: ONLINE / PHYSICAL / HYBRID
    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", length = 20)
    private LocationType locationType;

    // الإحداثيات (قد تكون null للكورسات الأونلاين)
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // العنوان الكامل (اختياري)
    @Column(name = "full_address", length = 500)
    private String fullAddress;
    // ===========================================

    // Relationships
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Enrollment> enrollments = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Review> reviews = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "course_categories",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @JsonIgnore
    private Set<Category> categories = new HashSet<>();

    private String status; // ACTIVE, INACTIVE, DELETED

    // ====== Constructors ======
    public Course() {}

    public Course(String title, String description, String location,
                  Integer duration, BigDecimal price, Boolean isFree, String instructor) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.duration = duration;
        this.price = price;
        this.isFree = isFree;
        this.instructor = instructor;
    }

    // ✨ Constructor محدث مع imageUrl
    public Course(String title, String description, String location,
                  Integer duration, BigDecimal price, Boolean isFree, String instructor, String imageUrl) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.duration = duration;
        this.price = price;
        this.isFree = isFree;
        this.instructor = instructor;
        this.imageUrl = imageUrl;
    }

    // ✨ Constructor إضافي يتضمن حقول الخريطة
    public Course(String title, String description, String location,
                  Integer duration, BigDecimal price, Boolean isFree, String instructor, String imageUrl,
                  LocationType locationType, Double latitude, Double longitude, String fullAddress) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.duration = duration;
        this.price = price;
        this.isFree = isFree;
        this.instructor = instructor;
        this.imageUrl = imageUrl;
        this.locationType = locationType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fullAddress = fullAddress;
    }

    public Course(Long id, String title, String description, String location, Integer duration, BigDecimal price,
                  Boolean isFree, String instructor, String imageUrl, LocalDateTime createdAt,
                  Set<Enrollment> enrollments, Set<Review> reviews, Set<Category> categories, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.duration = duration;
        this.price = price;
        this.isFree = isFree;
        this.instructor = instructor;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.enrollments = enrollments;
        this.reviews = reviews;
        this.categories = categories;
        this.status = status;
    }

    // ====== Getters and Setters ======
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Boolean getIsFree() { return isFree; }
    public void setIsFree(Boolean isFree) { this.isFree = isFree; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(Set<Enrollment> enrollments) { this.enrollments = enrollments; }

    public Set<Review> getReviews() { return reviews; }
    public void setReviews(Set<Review> reviews) { this.reviews = reviews; }

    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }

    public Boolean getFree() { return isFree; }
    public void setFree(Boolean free) { isFree = free; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // ====== Getters/Setters لحقول الخريطة ======
    public LocationType getLocationType() { return locationType; }
    public void setLocationType(LocationType locationType) { this.locationType = locationType; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
}
