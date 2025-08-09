package com.funchive.authserver.user.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDto {
    private String name;
    private String email;
    private String avatarUrl;
}
