package com.courseapp.coursesystem.dto;

import com.courseapp.coursesystem.entity.LocationType;

public class CourseMapDTO {
    private Long id;
    private String title;
    private String instructor;
    private Double latitude;
    private Double longitude;
    private String fullAddress;
    private LocationType locationType;

    public CourseMapDTO(Long id, String title, String instructor,
                        Double latitude, Double longitude, String fullAddress, LocationType locationType) {
        this.id = id;
        this.title = title;
        this.instructor = instructor;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fullAddress = fullAddress;
        this.locationType = locationType;
    }

    // getters فقط (أو Lombok إن تحب)
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getInstructor() { return instructor; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getFullAddress() { return fullAddress; }
    public LocationType getLocationType() { return locationType; }
}
