package com.funchive.authserver.auth.repository;

import com.funchive.authserver.auth.model.entity.GoogleOAuth2Identity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GoogleOAuth2IdentityRepository extends JpaRepository<GoogleOAuth2Identity, UUID> {

    boolean existsBySub(String sub);

    Optional<GoogleOAuth2Identity> findBySub(String sub);

    Optional<GoogleOAuth2Identity> findByEmail(String email);

}
