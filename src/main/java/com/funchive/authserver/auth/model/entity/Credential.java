package com.funchive.authserver.auth.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Credential {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

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
