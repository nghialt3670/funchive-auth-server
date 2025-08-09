package com.funchive.authserver.auth.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthorityDetailDto {
    private UUID id;
    private String name;
}
