package com.funchive.authserver.auth.repository;

import com.funchive.authserver.auth.model.entity.EmailPasswordCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailPasswordCredentialRepository extends JpaRepository<EmailPasswordCredential, UUID> {
    Optional<EmailPasswordCredential> findByEmail(String email);
}
