package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.LocationType;
import com.courseapp.coursesystem.exception.ApiResponse;
import com.courseapp.coursesystem.exception.CourseNotFoundException;
import com.courseapp.coursesystem.repository.CourseRepository;
import com.courseapp.coursesystem.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // استخدمناه فقط في الـ endpoints الجديدة الخاصة بالخريطة
    @Autowired
    private CourseRepository courseRepository;

    // ======================== CRUD & Search (القائمة الحالية) ========================

    // GET /api/courses - جلب جميع الكورسات
    @GetMapping
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        ApiResponse<List<Course>> response = new ApiResponse<>(courses, "Courses retrieved successfully");
        return ResponseEntity.ok(response);
    }

    // GET /api/courses/{id} - جلب كورس معين
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> getCourseById(@PathVariable Long id) {
        Optional<Course> course = courseService.getCourseById(id);

        if (course.isPresent()) {
            ApiResponse<Course> response = new ApiResponse<>(course.get(), "Course found successfully");
            return ResponseEntity.ok(response);
        } else {
            throw new CourseNotFoundException(id);
        }
    }

    // POST /api/courses - إضافة كورس جديد
    @PostMapping
    public ResponseEntity<ApiResponse<Course>> createCourse(@RequestBody Course course) {
        Course createdCourse = courseService.createCourse(course);
        ApiResponse<Course> response = new ApiResponse<>(createdCourse, "Course created successfully");
        return ResponseEntity.ok(response);
    }

    // PUT /api/courses/{id} - تحديث كورس موجود
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        Course updatedCourse = courseService.updateCourse(id, course);
        ApiResponse<Course> response = new ApiResponse<>(updatedCourse, "Course updated successfully");
        return ResponseEntity.ok(response);
    }

    // DELETE /api/courses/{id} - حذف كورس
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        ApiResponse<Void> response = new ApiResponse<>("Course deleted successfully");
        return ResponseEntity.ok(response);
    }

    // GET /api/courses/search - البحث في الكورسات
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Course>>> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean isFree) {

        List<Course> courses;
        String message;

        if (keyword != null && !keyword.trim().isEmpty()) {
            courses = courseService.searchCourses(keyword);
            message = "Search results for: " + keyword;
        } else if (location != null && !location.trim().isEmpty()) {
            courses = courseService.getCoursesByLocation(location);
            message = "Courses in: " + location;
        } else if (isFree != null) {
            courses = courseService.getCoursesByType(isFree);
            message = isFree ? "Free courses" : "Paid courses";
        } else {
            courses = courseService.getAllCourses();
            message = "All courses";
        }

        ApiResponse<List<Course>> response = new ApiResponse<>(courses, message);
        return ResponseEntity.ok(response);
    }

    // GET /api/courses/free - جلب الكورسات المجانية فقط
    @GetMapping("/free")
    public ResponseEntity<ApiResponse<List<Course>>> getFreeCourses() {
        List<Course> freeCourses = courseService.getCoursesByType(true);
        ApiResponse<List<Course>> response = new ApiResponse<>(freeCourses, "Free courses retrieved successfully");
        return ResponseEntity.ok(response);
    }

    // GET /api/courses/paid - جلب الكورسات المدفوعة فقط
    @GetMapping("/paid")
    public ResponseEntity<ApiResponse<List<Course>>> getPaidCourses() {
        List<Course> paidCourses = courseService.getCoursesByType(false);
        ApiResponse<List<Course>> response = new ApiResponse<>(paidCourses, "Paid courses retrieved successfully");
        return ResponseEntity.ok(response);
    }

    // ======================== Map Endpoints (الجديدة) ========================

    /**
     * GET /api/courses/with-coords
     * ترجع كل الكورسات اللي لها إحداثيات (latitude/longitude) فقط — مفيدة للخريطة.
     */
    @GetMapping("/with-coords")
    public ResponseEntity<ApiResponse<List<Course>>> getCoursesWithCoords() {
        List<Course> courses = courseRepository.findByLatitudeIsNotNullAndLongitudeIsNotNull();
        ApiResponse<List<Course>> response =
                new ApiResponse<>(courses, "Courses with coordinates retrieved successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/courses/by-location-type?type=PHYSICAL
     * ترجع الكورسات بحسب نوع الموقع: ONLINE, PHYSICAL, HYBRID
     */
    @GetMapping("/by-location-type")
    public ResponseEntity<ApiResponse<List<Course>>> getByLocationType(
            @RequestParam("type") LocationType type) {
        List<Course> courses = courseRepository.findByLocationType(type);
        ApiResponse<List<Course>> response =
                new ApiResponse<>(courses, "Courses filtered by location type: " + type);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/courses/map
     * Endpoint مرن للخريطة:
     * - types=PHYSICAL,HYBRID (اختياري - افتراضي PHYSICAL,HYBRID)
     * - swLat, swLng, neLat, neLng (اختياري - لتقييد النتائج داخل المربع الحالي على الخريطة)
     */
    @GetMapping("/map")
    public ResponseEntity<ApiResponse<List<Course>>> getForMap(
            @RequestParam(value = "types", required = false) String typesCsv,
            @RequestParam(value = "swLat", required = false) Double swLat,
            @RequestParam(value = "swLng", required = false) Double swLng,
            @RequestParam(value = "neLat", required = false) Double neLat,
            @RequestParam(value = "neLng", required = false) Double neLng
    ) {

        // 1) نجيب الكورسات اللي لها إحداثيات
        List<Course> courses = courseRepository.findByLatitudeIsNotNullAndLongitudeIsNotNull();

        // 2) نفلتر حسب الأنواع (إذا انطلبت)
        Set<LocationType> types = parseTypesOrDefault(typesCsv);
        courses = courses.stream()
                .filter(c -> c.getLocationType() != null && types.contains(c.getLocationType()))
                .collect(Collectors.toList());

        // 3) نفلتر حسب حدود الخريطة (إذا أرسلت)
        if (swLat != null && swLng != null && neLat != null && neLng != null) {
            double minLat = Math.min(swLat, neLat);
            double maxLat = Math.max(swLat, neLat);
            double minLng = Math.min(swLng, neLng);
            double maxLng = Math.max(swLng, neLng);

            courses = courses.stream()
                    .filter(c ->
                            c.getLatitude() != null && c.getLongitude() != null &&
                                    c.getLatitude()  >= minLat && c.getLatitude()  <= maxLat &&
                                    c.getLongitude() >= minLng && c.getLongitude() <= maxLng
                    )
                    .collect(Collectors.toList());
        }

        ApiResponse<List<Course>> response =
                new ApiResponse<>(courses, "Map courses retrieved successfully");
        return ResponseEntity.ok(response);
    }

    // Helper: قراءة الأنواع من الـ query أو استخدام الافتراضي (PHYSICAL, HYBRID)
    private Set<LocationType> parseTypesOrDefault(String typesCsv) {
        if (typesCsv == null || typesCsv.isBlank()) {
            return new HashSet<>(Arrays.asList(LocationType.PHYSICAL, LocationType.HYBRID));
        }
        return Arrays.stream(typesCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .map(LocationType::valueOf)
                .collect(Collectors.toSet());
    }
}
