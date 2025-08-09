package com.funchive.authserver.user.repository;

import com.funchive.authserver.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findBySlug(String slug);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
