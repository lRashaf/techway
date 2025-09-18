package com.courseapp.coursesystem.controller;

import com.courseapp.coursesystem.entity.User;
import com.courseapp.coursesystem.exception.ApiResponse;
import com.courseapp.coursesystem.service.JwtService;
import com.courseapp.coursesystem.service.CustomUserDetailsService;
import com.courseapp.coursesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    // POST /api/auth/register - تسجيل مستخدم جديد
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        try {
            // إنشاء مستخدم جديد
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setRole("USER"); // default role

            User createdUser = userService.createUser(user);

            // إرجاع البيانات بدون كلمة المرور
            UserResponse userResponse = new UserResponse(
                    createdUser.getId(),
                    createdUser.getName(),
                    createdUser.getEmail(),
                    createdUser.getRole(),
                    createdUser.getCreatedAt()
            );

            ApiResponse<UserResponse> response = new ApiResponse<>(userResponse, "User registered successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            ApiResponse<UserResponse> response = new ApiResponse<>("Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // POST /api/auth/login - تسجيل الدخول
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("🔐 Login attempt for email: " + request.getEmail());

            // تحقق من بيانات الدخول
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // توليد JWT
            String jwtToken = jwtService.generateToken(userDetails);

            // تجهيز البيانات
            UserResponse userResponse = new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getCreatedAt()
            );

            LoginResponse loginResponse = new LoginResponse(userResponse, jwtToken);

            ApiResponse<LoginResponse> response = new ApiResponse<>(loginResponse, "Login successful");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            ApiResponse<LoginResponse> response = new ApiResponse<>("Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }
    }



}

    // GET /api/auth/profile - جلب بيانات المستخدم الحالي
//


// Request/Response DTOs
class RegisterRequest {
    private String name;
    private String email;
    private String password;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class LoginRequest {
    private String email;
    private String password;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class UpdateProfileRequest {
    private String name;
    private String email;
    private String password;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private java.time.LocalDateTime createdAt;

    public UserResponse(Long id, String name, String email, String role, java.time.LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
}

class LoginResponse {
    private UserResponse user;
    private String token;

    public LoginResponse(UserResponse user, String token) {
        this.user = user;
        this.token = token;
    }

    // Getters and Setters
    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}