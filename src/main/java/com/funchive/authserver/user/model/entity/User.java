package com.funchive.authserver.user.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@EntityListeners({AuditingEntityListener.class})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(unique = true)
    private String email;

    private String avatarUrl;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private UUID createdBy;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant lastModifiedDate;

    @LastModifiedBy
    @Column(nullable = false)
    private UUID lastModifiedBy;

}
