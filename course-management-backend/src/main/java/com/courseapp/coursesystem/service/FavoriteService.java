// FavoriteService.java
package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.Favorite;
import com.courseapp.coursesystem.entity.Course;
import java.util.List;

public interface FavoriteService {

    // إدارة المفضلة
    Favorite addToFavorites(Favorite favorite);
    void removeFromFavorites(Long userId, Long courseId);
    boolean isCourseInUserFavorites(Long userId, Long courseId);

    // جلب البيانات
    List<Course> getUserFavoriteCourses(Long userId);
    List<Favorite> getUserFavorites(Long userId);

    // إحصائيات
    long getUserFavoritesCount(Long userId);
    long getCourseFavoritesCount(Long courseId);
    List<Course> getMostFavoritedCourses();
}