// FavoriteServiceImpl.java
package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.Favorite;
import com.courseapp.coursesystem.entity.Course;
import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.exception.ValidationException;
import com.courseapp.coursesystem.repository.CourseRepository;
import com.courseapp.coursesystem.repository.FavoriteRepository;
import com.courseapp.coursesystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Favorite addToFavorites(Favorite favorite) {
        validateFavorite(favorite);

        // التحقق من عدم وجود المفضلة مسبقاً
        if (favoriteRepository.existsByUserAndCourse(favorite.getUser(), favorite.getCourse())) {
            throw new ValidationException("Course is already in user's favorites");
        }

        return favoriteRepository.save(favorite);
    }

    public Favorite addFavorite(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Favorite favorite = new Favorite(user, course);
        return favoriteRepository.save(favorite);
    }

    @Override
    public void removeFromFavorites(Long userId, Long courseId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("User ID must be a positive number");
        }
        if (courseId == null || courseId <= 0) {
            throw new ValidationException("Course ID must be a positive number");
        }

        if (!favoriteRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new ValidationException("Favorite not found");
        }

        favoriteRepository.deleteByUserIdAndCourseId(userId, courseId);
    }

    @Override
    public boolean isCourseInUserFavorites(Long userId, Long courseId) {
        if (userId == null || userId <= 0 || courseId == null || courseId <= 0) {
            return false;
        }
        return favoriteRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    @Override
    public List<Course> getUserFavoriteCourses(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("User ID must be a positive number");
        }

        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return favorites.stream()
                .map(Favorite::getCourse)
                .collect(Collectors.toList());
    }

    @Override
    public List<Favorite> getUserFavorites(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("User ID must be a positive number");
        }
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public long getUserFavoritesCount(Long userId) {
        if (userId == null || userId <= 0) {
            return 0;
        }
        return favoriteRepository.countByUserId(userId);
    }

    @Override
    public long getCourseFavoritesCount(Long courseId) {
        if (courseId == null || courseId <= 0) {
            return 0;
        }
        return favoriteRepository.countByCourseId(courseId);
    }

    @Override
    public List<Course> getMostFavoritedCourses() {
        List<Object[]> results = favoriteRepository.findMostFavoritedCourses();
        return results.stream()
                .limit(10) // أول 10 كورسات
                .map(result -> (Course) result[0])
                .collect(Collectors.toList());
    }

    // التحقق من صحة المفضلة
    private void validateFavorite(Favorite favorite) {
        if (favorite == null) {
            throw new ValidationException("Favorite data is required");
        }

        if (favorite.getUser() == null || favorite.getUser().getId() == null) {
            throw new ValidationException("User is required");
        }

        if (favorite.getCourse() == null || favorite.getCourse().getId() == null) {
            throw new ValidationException("Course is required");
        }
    }
}