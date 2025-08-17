package com.funchive.authserver.auth.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class EmailPasswordRegistrationDto {
    
    private UUID id;
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email must not be blank")
    @Size(max = 100, message = "Email must be at most 100 characters")
    private String email;
    
    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;
    
    @NotBlank(message = "Verification code must not be blank")
    @Size(min = 6, max = 6, message = "Verification code must be 6 characters")
    private String verificationCode;
    
    @NotBlank(message = "Name must not be blank")
    @Size(max = 50, message = "Name must be at most 50 characters")
    private String name;
    
    private LocalDate birthDate;
    
    @Size(max = 255, message = "Avatar URL must be at most 255 characters")
    private String avatarUrl;
}
