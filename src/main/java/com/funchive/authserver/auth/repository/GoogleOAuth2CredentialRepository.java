package com.funchive.authserver.auth.repository;

import com.funchive.authserver.auth.model.entity.GoogleOAuth2Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GoogleOAuth2CredentialRepository extends JpaRepository<GoogleOAuth2Credential, UUID> {
    Optional<GoogleOAuth2Credential> findByEmail(String email);
}
