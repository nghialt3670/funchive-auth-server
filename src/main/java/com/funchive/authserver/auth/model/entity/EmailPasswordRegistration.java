package com.funchive.authserver.auth.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity(name = "email_password_registrations")
public class EmailPasswordRegistration extends Registration {

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String verificationCode;

}
