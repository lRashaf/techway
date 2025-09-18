// UserService.java
package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    // إدارة المستخدمين
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);

    // التوثيق
    User authenticate(String email, String password);
    boolean emailExists(String email);

    // إدارة الأدوار
    List<User> getUsersByRole(String role);
    User updateUserRole(Long id, String role);
}