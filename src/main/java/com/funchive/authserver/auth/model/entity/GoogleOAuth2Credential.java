package com.funchive.authserver.auth.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "google_oauth2_credentials")
public class GoogleOAuth2Credential extends Credential {

    @Column(nullable = false, unique = true)
    private String sub;

    @Column(nullable = false)
    private String name;

    private String givenName;

    private String familyName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified;

    private String picture;

}
