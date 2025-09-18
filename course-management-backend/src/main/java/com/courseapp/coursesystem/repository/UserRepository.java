package com.courseapp.coursesystem.repository;

import com.courseapp.coursesystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // للبحث عن مستخدم بالإيميل
    Optional<User> findByEmail(String email);

    // للتحقق من وجود إيميل
    boolean existsByEmail(String email);

    // للبحث بالدور
    java.util.List<User> findByRole(String role);


}