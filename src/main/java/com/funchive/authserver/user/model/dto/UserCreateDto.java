package com.funchive.authserver.user.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserCreateDto {

    @NotBlank(message = "User Name must not be blank")
    @Size(max = 50, message = "User Name must be at most 50 characters")
    private String name;

    @Email(message = "Invalid user email format")
    @Size(max = 100, message = "User email must be at most 100 characters")
    private String email;

    @Size(max = 255, message = "User avatar URL must be at most 255 characters")
    private String avatarUrl;

}
