package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.dto.CourseProgressView;
import com.courseapp.coursesystem.dto.LessonProgressUpsertRequest;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.repository.UserRepository;
import com.courseapp.coursesystem.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // إضافة CORS
public class ProgressController {

    private final ProgressService progressService;
    private final UserRepository userRepository;

    @PostMapping
    public CourseProgressView upsert(
            @RequestBody LessonProgressUpsertRequest req,
            @RequestParam(required = false) Long userId // دعم معامل اختياري
    ) {
        Long actualUserId = userId != null ? userId : currentUserId();
        return progressService.upsertLessonProgress(actualUserId, req);
    }

    @GetMapping("/course/{courseId}")
    public CourseProgressView get(
            @PathVariable Long courseId,
            @RequestParam(required = false) Long userId // دعم معامل اختياري
    ) {
        Long actualUserId = userId != null ? userId : currentUserId();
        return progressService.getCourseProgress(actualUserId, courseId);
    }

    private Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String username;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) {
            username = ud.getUsername(); // غالبًا الإيميل
        } else {
            username = auth.getName();
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return user.getId();
    }
}