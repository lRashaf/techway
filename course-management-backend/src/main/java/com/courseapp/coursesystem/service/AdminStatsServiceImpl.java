package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.dto.*;
import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.Enrollment;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.repository.CourseRepository;
import com.courseapp.coursesystem.repository.EnrollmentRepository;
import com.courseapp.coursesystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdminStatsServiceImpl implements AdminStatsService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    private static final ZoneId ZONE = ZoneId.systemDefault();
    private static final DateTimeFormatter LABEL_FMT = DateTimeFormatter.ofPattern("dd/MM");

    public AdminStatsServiceImpl(EnrollmentRepository enrollmentRepository,
                                 CourseRepository courseRepository,
                                 UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    // ================== Overview ==================
    @Override
    public AdminOverviewDTO getOverview() {
        long totalUsers = safeCountUsers();
        List<Course> courses = courseRepository.findAll();
        List<Enrollment> enrollments = enrollmentRepository.findAll();

        long totalCourses = courses.size();
        long freeCourses = courses.stream().filter(c -> Boolean.TRUE.equals(c.getIsFree())).count();
        long paidCourses = totalCourses - freeCourses;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from30 = now.minusDays(30);
        long newEnrollments30d = enrollments.stream()
                .filter(e -> e.getEnrolledAt() != null && !e.getEnrolledAt().isBefore(from30) && !e.getEnrolledAt().isAfter(now))
                .count();

        BigDecimal totalRevenue = enrollments.stream()
                .map(Enrollment::getCourse)
                .filter(Objects::nonNull)
                .filter(c -> !Boolean.TRUE.equals(c.getIsFree()))
                .map(Course::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<RecentCourseDTO> recentCourses = courses.stream()
                .sorted(Comparator.comparing((Course c) ->
                        Optional.ofNullable(c.getCreatedAt()).orElse(LocalDateTime.MIN)).reversed())
                .limit(5)
                .map(c -> new RecentCourseDTO(
                        c.getId(),
                        nullToDash(c.getTitle()),
                        nullToDash(c.getInstructor()),
                        Boolean.TRUE.equals(c.getIsFree()),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return new AdminOverviewDTO(
                totalUsers, totalCourses, freeCourses, paidCourses,
                newEnrollments30d, totalRevenue, recentCourses
        );
    }

    // ================== Trend ==================
    @Override
    public TrendDTO getEnrollmentsTrend(String rangeKey) {
        int days = normalizeRange(rangeKey); // 7 / 30 / 90
        LocalDate today = LocalDate.now(ZONE);
        LocalDate start = today.minusDays(days - 1); // شامل

        List<String> labels = new ArrayList<>(days);
        Map<LocalDate, Integer> indexOfDay = new HashMap<>();
        LocalDate d = start;
        int idx = 0;
        while (!d.isAfter(today)) {
            labels.add(d.format(LABEL_FMT));
            indexOfDay.put(d, idx++);
            d = d.plusDays(1);
        }
        long[] counts = new long[labels.size()];

        List<Enrollment> all = enrollmentRepository.findAll();
        for (Enrollment e : all) {
            LocalDateTime at = e.getEnrolledAt();
            if (at == null) continue;
            LocalDate day = at.toLocalDate();
            if (day.isBefore(start) || day.isAfter(today)) continue;
            Integer i = indexOfDay.get(day);
            if (i != null) counts[i] += 1;
        }

        List<Long> data = Arrays.stream(counts).boxed().collect(Collectors.toList());
        return new TrendDTO(labels, data);
    }

    // ================== Distribution ==================
    @Override
    public DistributionDTO getCourseDistribution() {
        List<Enrollment> enrollments = enrollmentRepository.findAll();

        Map<String, Long> byCategory = enrollments.stream()
                .map(Enrollment::getCourse)
                .filter(Objects::nonNull)
                .map(this::categoryLabelFromCourse) // ← نقرأ من Set<Category> عندك
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<Map.Entry<String, Long>> sorted = byCategory.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        List<String> labels = sorted.stream().map(Map.Entry::getKey).collect(Collectors.toList());
        List<Long> data = sorted.stream().map(Map.Entry::getValue).collect(Collectors.toList());
        return new DistributionDTO(labels, data);
    }

    // ================== Top Courses ==================
    @Override
    public List<TopCourseRowDTO> getTopCourses(int limit) {
        List<Enrollment> enrollments = enrollmentRepository.findAll();

        Map<Long, List<Enrollment>> byCourseId = enrollments.stream()
                .filter(e -> e.getCourse() != null && e.getCourse().getId() != null)
                .collect(Collectors.groupingBy(e -> e.getCourse().getId()));

        Map<Long, Course> courseMap = courseRepository.findAll().stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(Course::getId, Function.identity(), (a, b) -> a));

        List<TopCourseRowDTO> rows = new ArrayList<>();
        for (Map.Entry<Long, List<Enrollment>> entry : byCourseId.entrySet()) {
            Long courseId = entry.getKey();
            Course c = courseMap.get(courseId);
            if (c == null) continue;

            long students = entry.getValue().size();
            BigDecimal revenue = entry.getValue().stream()
                    .map(Enrollment::getCourse)
                    .filter(Objects::nonNull)
                    .filter(cc -> !Boolean.TRUE.equals(cc.getIsFree()))
                    .map(Course::getPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            rows.add(new TopCourseRowDTO(
                    courseId,
                    nullToDash(c.getTitle()),
                    categoryLabelFromCourse(c),
                    students,
                    revenue,
                    safeStatus(c),
                    null // ما عندك rating، خلّها null
            ));
        }

        rows.sort(Comparator
                .comparingLong((TopCourseRowDTO top) -> top.getStudents())
                .reversed()
                .thenComparing(
                        (TopCourseRowDTO top) -> top.getRevenue() == null ? BigDecimal.ZERO : top.getRevenue(),
                        Comparator.reverseOrder()
                )
        );


        if (limit > 0 && rows.size() > limit) return rows.subList(0, limit);
        return rows;
    }

    // ================== Recent Activity ==================
    @Override
    public List<ActivityItemDTO> getRecentActivity(int limit) {
        List<ActivityItemDTO> items = new ArrayList<>();

        List<Enrollment> enrollments = enrollmentRepository.findAll();
        enrollments.stream()
                .filter(e -> e.getEnrolledAt() != null)
                .sorted(Comparator.comparing(Enrollment::getEnrolledAt).reversed())
                .limit(limit)
                .forEach(e -> {
                    String userName = userNameOrDash(e.getUser());
                    String courseTitle = e.getCourse() != null ? nullToDash(e.getCourse().getTitle()) : "—";
                    items.add(new ActivityItemDTO("ENROLL", userName, courseTitle, e.getEnrolledAt(), null));
                });

        List<Course> courses = courseRepository.findAll();
        courses.stream()
                .filter(c -> c.getCreatedAt() != null)
                .sorted(Comparator.comparing(Course::getCreatedAt).reversed())
                .limit(limit)
                .forEach(c -> {
                    items.add(new ActivityItemDTO("COURSE_CREATE", null, nullToDash(c.getTitle()), c.getCreatedAt(), "Admin"));
                });

        items.sort(Comparator.comparing(ActivityItemDTO::getAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        if (limit > 0 && items.size() > limit) return items.subList(0, limit);
        return items;
    }

    // ================== Helpers ==================
    private int normalizeRange(String rangeKey) {
        if (rangeKey == null) return 30;
        String k = rangeKey.trim().toLowerCase();
        switch (k) {
            case "7d": return 7;
            case "90d": return 90;
            case "30d":
            default: return 30;
        }
    }

    private long safeCountUsers() {
        try { return userRepository.count(); }
        catch (Exception ex) { return 0L; }
    }

    private String nullToDash(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    private String safeStatus(Course c) {
        String st = (c.getStatus() == null || c.getStatus().isBlank()) ? "Active" : c.getStatus();
        return st;
    }

    private String userNameOrDash(User u) {
        if (u == null) return "—";
        if (u.getName() != null && !u.getName().isBlank()) return u.getName();
        if (u.getEmail() != null && !u.getEmail().isBlank()) return u.getEmail();
        return "—";
    }

    // نقرأ أول كاتيجري من Set<Category> بدون ما نفترض شكل الـ Category
    private String categoryLabelFromCourse(Course c) {
        try {
            Object setObj = c.getCategories(); // Set<...>
            if (!(setObj instanceof Collection<?> col) || col.isEmpty()) return "غير مصنّف";
            Object first = col.iterator().next();
            // جرّب getName() ثم getTitle()
            try {
                Method m = first.getClass().getMethod("getName");
                Object name = m.invoke(first);
                if (name instanceof String s && !s.isBlank()) return s;
            } catch (NoSuchMethodException ignore) {
                try {
                    Method m2 = first.getClass().getMethod("getTitle");
                    Object name2 = m2.invoke(first);
                    if (name2 instanceof String s2 && !s2.isBlank()) return s2;
                } catch (NoSuchMethodException ignore2) { /* تجاهل */ }
            }
        } catch (Exception ignore) { /* تجاهل */ }
        return "غير مصنّف";
    }
}
