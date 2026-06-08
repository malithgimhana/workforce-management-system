package com.flexiwork.repository;

import com.flexiwork.enums.DocumentStatus;
import com.flexiwork.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByNic(String nic);
    Optional<User> findByEmailOrPhone(String email, String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByNic(String nic);

    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findRecentUsers(Pageable pageable);

    List<User> findByDocStatus(DocumentStatus docStatus);
    List<User> findByDocStatusAndDocSubmittedAtBefore(DocumentStatus docStatus, LocalDateTime cutoff);
}
