package com.funchive.authserver.auth.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class EmailPasswordCredential extends Credential {

    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean isPasswordExpired;

}
