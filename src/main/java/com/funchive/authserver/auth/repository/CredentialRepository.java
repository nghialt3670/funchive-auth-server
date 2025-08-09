package com.funchive.authserver.auth.repository;

import com.funchive.authserver.auth.model.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CredentialRepository extends JpaRepository<Credential, UUID> {
}
