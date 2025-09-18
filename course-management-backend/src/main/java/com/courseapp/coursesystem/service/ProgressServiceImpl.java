package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.dto.CourseProgressView;
import com.courseapp.coursesystem.dto.LessonProgressUpsertRequest;
import com.courseapp.coursesystem.dto.ModuleProgressView;
import com.courseapp.coursesystem.entity.Lesson;
import com.courseapp.coursesystem.entity.LessonProgress;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.repository.LessonProgressRepository;
import com.courseapp.coursesystem.repository.LessonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgressServiceImpl implements ProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;

    @Override
    @Transactional
    public CourseProgressView upsertLessonProgress(Long userId, LessonProgressUpsertRequest req) {
        log.info("Updating lesson progress for user {} lesson {} to {}%",
                userId, req.getLessonId(), req.getProgressPercent());

        Lesson lesson = lessonRepository.findById(req.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found: " + req.getLessonId()));

        LessonProgress lp = lessonProgressRepository
                .findByUserIdAndLessonId(userId, lesson.getId())
                .orElseGet(() -> {
                    log.info("Creating new lesson progress for user {} lesson {}", userId, lesson.getId());
                    LessonProgress newProgress = new LessonProgress();
                    User user = new User();
                    user.setId(userId);
                    newProgress.setUser(user);
                    newProgress.setLesson(lesson);
                    return newProgress;
                });

        // تحديث النسبة المئوية
        int oldPercent = lp.getProgressPercent();
        int newPercent = Math.max(0, Math.min(100, req.getProgressPercent()));
        lp.setProgressPercent(newPercent);

        // تسجيل تاريخ الإكمال عند الوصول لـ 100%
        if (newPercent == 100 && lp.getCompletedAt() == null) {
            lp.setCompletedAt(Instant.now());
            log.info("Lesson {} completed by user {}", lesson.getId(), userId);
        }

        // إلغاء تاريخ الإكمال إذا انخفضت النسبة عن 100%
        if (newPercent < 100 && lp.getCompletedAt() != null) {
            lp.setCompletedAt(null);
            log.info("Lesson {} completion reset for user {}", lesson.getId(), userId);
        }

        lessonProgressRepository.save(lp);

        log.info("Progress updated from {}% to {}% for lesson {}", oldPercent, newPercent, lesson.getId());

        return calcCourseProgress(userId, lesson.getModule().getCourse().getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseProgressView getCourseProgress(Long userId, Long courseId) {
        log.info("Fetching course progress for user {} course {}", userId, courseId);
        return calcCourseProgress(userId, courseId);
    }

    private CourseProgressView calcCourseProgress(Long userId, Long courseId) {
        // جلب جميع دروس الكورس
        List<Lesson> allLessons = lessonRepository.findByModuleCourseId(courseId);
        log.info("Found {} lessons in course {}", allLessons.size(), courseId);

        if (allLessons.isEmpty()) {
            log.warn("No lessons found for course {}", courseId);
            return new CourseProgressView(courseId, 0, new ArrayList<>(), new HashMap<>());
        }

        // جلب تقدم المستخدم في جميع دروس الكورس
        Map<Long, Integer> lessonProgressMap = new HashMap<>();
        List<LessonProgress> userProgress = lessonProgressRepository.findByUserAndCourse(userId, courseId);

        for (LessonProgress progress : userProgress) {
            lessonProgressMap.put(progress.getLesson().getId(), progress.getProgressPercent());
        }

        log.info("Found progress data for {} lessons out of {} total", lessonProgressMap.size(), allLessons.size());

        // تجميع الدروس حسب الوحدة
        Map<Long, List<Lesson>> lessonsByModule = allLessons.stream()
                .collect(Collectors.groupingBy(lesson -> lesson.getModule().getId()));

        List<ModuleProgressView> moduleProgressList = new ArrayList<>();
        int totalProgressPoints = 0;  // مجموع نقاط التقدم
        int totalLessons = allLessons.size();

        // حساب تقدم كل وحدة
        for (Map.Entry<Long, List<Lesson>> entry : lessonsByModule.entrySet()) {
            Long moduleId = entry.getKey();
            List<Lesson> moduleLessons = entry.getValue();

            int moduleProgressSum = 0;
            for (Lesson lesson : moduleLessons) {
                int lessonProgress = lessonProgressMap.getOrDefault(lesson.getId(), 0);
                moduleProgressSum += lessonProgress;
                totalProgressPoints += lessonProgress;
            }

            int modulePercent = moduleLessons.isEmpty() ? 0 : (moduleProgressSum / moduleLessons.size());
            moduleProgressList.add(new ModuleProgressView(moduleId, modulePercent));

            log.debug("Module {} progress: {}% ({} lessons)", moduleId, modulePercent, moduleLessons.size());
        }

        // حساب نسبة التقدم الإجمالي للكورس
        int coursePercent = totalLessons == 0 ? 0 : (totalProgressPoints / totalLessons);

        // إنشاء خريطة تقدم الدروس للعرض في Frontend
        Map<Long, Integer> lessonsProgressMap = allLessons.stream()
                .collect(Collectors.toMap(
                        Lesson::getId,
                        lesson -> lessonProgressMap.getOrDefault(lesson.getId(), 0)
                ));

        log.info("Course {} overall progress: {}% for user {}", courseId, coursePercent, userId);

        return new CourseProgressView(courseId, coursePercent, moduleProgressList, lessonsProgressMap);
    }
}