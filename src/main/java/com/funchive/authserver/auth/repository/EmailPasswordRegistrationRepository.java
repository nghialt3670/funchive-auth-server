package com.funchive.authserver.auth.repository;

import com.funchive.authserver.auth.model.entity.EmailPasswordRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface EmailPasswordRegistrationRepository extends JpaRepository<EmailPasswordRegistration, UUID> {

    boolean existsByEmail(String email);
    
    Optional<EmailPasswordRegistration> findByEmail(String email);
    
    @Modifying
    @Query("DELETE FROM email_password_registrations r WHERE r.id = :id")
    void deleteById(UUID id);
    
    @Modifying
    @Query("DELETE FROM email_password_registrations r WHERE r.createdDate < :cutoffDate")
    void deleteExpiredRegistrations(Instant cutoffDate);

}
