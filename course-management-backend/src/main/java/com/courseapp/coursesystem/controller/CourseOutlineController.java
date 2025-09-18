package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.dto.CourseOutlineDto;
import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.Module;
import com.courseapp.coursesystem.exception.ApiResponse;
import com.courseapp.coursesystem.exception.CourseNotFoundException;
import com.courseapp.coursesystem.mapper.OutlineMapper;
import com.courseapp.coursesystem.service.CourseService;
import com.courseapp.coursesystem.service.ModuleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseOutlineController {

    private final CourseService courseService;
    private final ModuleService moduleService;

    public CourseOutlineController(CourseService courseService, ModuleService moduleService) {
        this.courseService = courseService;
        this.moduleService = moduleService;
    }

    /**
     * GET /api/courses/{courseId}/outline
     * يرجّع Outline مرتب (Modules + Lessons) مع منطق القفل:
     * - لو الكورس ONLINE/HYBRID: الوحدات المجانية isFree=true مفتوحة، غيرها مقفولة
     * - لو الكورس PHYSICAL: كل الوحدات/الدروس تعتبر "مقفولة" (لا فيديو)، والـ UI يعرض زر الخريطة/الدفع
     */
    @GetMapping("/{courseId}/outline")
    public ResponseEntity<ApiResponse<CourseOutlineDto>> getCourseOutline(@PathVariable Long courseId) {
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        List<Module> modules = moduleService.findByCourseId(courseId);

        // منطق القفل: نقفل المدفوعة إذا لم تكن الوحدة مجانية
        boolean lockPaidModules = (course.getLocationType() != null && course.getLocationType().name().equals("PHYSICAL"))
                ? true
                : false; // PHYSICAL → كله مقفول (عرض معلومات فقط)

        CourseOutlineDto dto = OutlineMapper.toCourseOutlineDto(course, modules, lockPaidModules);

        return ResponseEntity.ok(new ApiResponse<>(dto, "Course outline retrieved successfully"));
    }
}
