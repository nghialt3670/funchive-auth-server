package com.funchive.authserver.user.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserDetailDto {
    private UUID id;
    private String slug;
    private String name;
    private String email;
    private String avatarUrl;
}
