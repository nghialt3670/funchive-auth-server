package com.funchive.authserver.auth.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationStepDto {
    
    public enum Step {
        EMAIL,
        PASSWORD,
        VERIFICATION,
        PROFILE,
        COMPLETED
    }
    
    private Step currentStep;
    private boolean emailSet;
    private boolean passwordSet;
    private boolean emailVerified;
    private boolean profileSet;
}
