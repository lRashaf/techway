// UserServiceImpl.java
package com.courseapp.coursesystem.service;

import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.exception.ValidationException;
import com.courseapp.coursesystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("User ID must be a positive number");
        }
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    @Override
    public User createUser(User user) {
        validateUser(user);

        // التحقق من عدم وجود الإيميل مسبقاً
        if (userRepository.existsByEmail(user.getEmail().toLowerCase())) {
            throw new ValidationException("Email already exists");
        }

        // تشفير كلمة المرور
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmail(user.getEmail().toLowerCase());

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        if (id == null || id <= 0) {
            throw new ValidationException("User ID must be a positive number");
        }

        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new ValidationException("User not found with id: " + id);
        }

        validateUser(user);

        User userToUpdate = existingUser.get();

        // التحقق من الإيميل إذا تم تغييره
        if (!userToUpdate.getEmail().equals(user.getEmail().toLowerCase())) {
            if (userRepository.existsByEmail(user.getEmail().toLowerCase())) {
                throw new ValidationException("Email already exists");
            }
        }

        userToUpdate.setName(user.getName());
        userToUpdate.setEmail(user.getEmail().toLowerCase());

        // تشفير كلمة المرور الجديدة إذا تم تغييرها
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(userToUpdate);
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("User ID must be a positive number");
        }

        if (!userRepository.existsById(id)) {
            throw new ValidationException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }

    @Override
    public User authenticate(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }

        Optional<User> user = userRepository.findByEmail(email.toLowerCase());

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user.get();
        }

        return null; // Authentication failed
    }

    @Override
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userRepository.existsByEmail(email.toLowerCase());
    }

    @Override
    public List<User> getUsersByRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new ValidationException("Role cannot be empty");
        }
        return userRepository.findByRole(role.toUpperCase());
    }

    @Override
    public User updateUserRole(Long id, String role) {
        if (id == null || id <= 0) {
            throw new ValidationException("User ID must be a positive number");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new ValidationException("Role cannot be empty");
        }

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ValidationException("User not found with id: " + id);
        }

        User userToUpdate = user.get();
        userToUpdate.setRole(role.toUpperCase());

        return userRepository.save(userToUpdate);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new ValidationException("User data is required");
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new ValidationException("Name is required");
        }

        if (user.getName().length() > 100) {
            throw new ValidationException("Name cannot exceed 100 characters");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }

        if (user.getEmail().length() > 100) {
            throw new ValidationException("Email cannot exceed 100 characters");
        }

        // التحقق من صيغة الإيميل
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Invalid email format");
        }

        if (user.getPassword() != null && user.getPassword().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }
    }
}