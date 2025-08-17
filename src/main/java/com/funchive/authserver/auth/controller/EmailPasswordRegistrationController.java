package com.funchive.authserver.auth.controller;

import com.funchive.authserver.auth.model.dto.EmailPasswordRegistrationDto;
import com.funchive.authserver.auth.model.dto.RegistrationStepDto;
import com.funchive.authserver.auth.model.entity.EmailPasswordRegistration;
import com.funchive.authserver.auth.service.EmailPasswordRegistrationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/registration/email-password")
public class EmailPasswordRegistrationController {
    
    private final EmailPasswordRegistrationService registrationService;

    private static final String REGISTRATION_ID_KEY = "registrationId";

    @GetMapping
    public String redirectToEmailStep() {
        return "redirect:/registration/email-password/email";
    }

    @GetMapping("/email")
    public String showEmailStep(Model model, HttpSession session) {
        session.removeAttribute(REGISTRATION_ID_KEY);
        model.addAttribute("registrationDto", EmailPasswordRegistrationDto.builder().build());
        return "registration/email-password/email";
    }
    
    @PostMapping("/email")
    public String processEmailStep(@Valid @ModelAttribute("registrationDto") EmailPasswordRegistrationDto dto,
                                   BindingResult result,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        
        if (result.hasFieldErrors("email")) {
            return "registration/email-password/email";
        }
        
        try {
            if (registrationService.checkEmailExists(dto.getEmail())) {
                result.rejectValue("email", "email.exists", "Email is already registered");
                return "registration/email-password/email";
            }
            
            UUID registrationId = registrationService.startRegistration(dto.getEmail());
            session.setAttribute(REGISTRATION_ID_KEY, registrationId);
            
            return "redirect:/registration/email-password/password";
            
        } catch (Exception e) {
            log.error("Error processing email step", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred. Please try again.");
            return "redirect:/registration/email-password/email";
        }
    }

    @GetMapping("/password")
    public String showPasswordStep(Model model, HttpSession session) {
        UUID registrationId = getRegistrationIdFromSession(session);
        if (registrationId == null) {
            return "redirect:/registration/email-password/email";
        }
        
        model.addAttribute("registrationDto", EmailPasswordRegistrationDto.builder().build());
        return "registration/email-password/password";
    }
    
    @PostMapping("/password")
    public String processPasswordStep(@Valid @ModelAttribute("registrationDto") EmailPasswordRegistrationDto dto,
                                      BindingResult result,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        
        UUID registrationId = getRegistrationIdFromSession(session);
        if (registrationId == null) {
            return "redirect:/registration/email-password/email";
        }
        
        if (result.hasFieldErrors("password")) {
            return "registration/email-password/password";
        }
        
        try {
            registrationService.updateWithPassword(registrationId, dto.getPassword());
            registrationService.sendVerificationCode(registrationId);
            
            return "redirect:/registration/email-password/verification";
            
        } catch (Exception e) {
            log.error("Error processing password step", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred. Please try again.");
            return "redirect:/registration/email-password/password";
        }
    }
    
    /**
     * Email verification step
     */
    @GetMapping("/verification")
    public String showVerificationStep(Model model, HttpSession session) {
        UUID registrationId = getRegistrationIdFromSession(session);
        if (registrationId == null) {
            return "redirect:/registration/email-password/email";
        }
        
        try {
            EmailPasswordRegistration registration = registrationService.getRegistration(registrationId);
            model.addAttribute("email", registration.getEmail());
            model.addAttribute("registrationDto", EmailPasswordRegistrationDto.builder().build());
            return "registration/email-password/verification";
        } catch (Exception e) {
            log.error("Error showing verification step", e);
            return "redirect:/registration/email-password/email";
        }
    }
    
    @PostMapping("/verification")
    public String processVerificationStep(@Valid @ModelAttribute("registrationDto") EmailPasswordRegistrationDto dto,
                                          BindingResult result,
                                          HttpSession session,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        
        UUID registrationId = getRegistrationIdFromSession(session);
        if (registrationId == null) {
            return "redirect:/registration/email-password/email";
        }
        
        if (result.hasFieldErrors("verificationCode")) {
            try {
                EmailPasswordRegistration registration = registrationService.getRegistration(registrationId);
                model.addAttribute("email", registration.getEmail());
            } catch (Exception e) {
                return "redirect:/registration/email-password/email";
            }
            return "registration/email-password/verification";
        }
        
        try {
            boolean isValid = registrationService.verifyEmail(registrationId, dto.getVerificationCode());
            
            if (!isValid) {
                result.rejectValue("verificationCode", "code.invalid", "Invalid verification code");
                EmailPasswordRegistration registration = registrationService.getRegistration(registrationId);
                model.addAttribute("email", registration.getEmail());
                return "registration/email-password/verification";
            }
            
            return "redirect:/registration/email-password/profile";
            
        } catch (Exception e) {
            log.error("Error processing verification step", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred. Please try again.");
            return "redirect:/registration/email-password/verification";
        }
    }
    
    @PostMapping("/verification/resend")
    public String resendVerificationCode(HttpSession session, RedirectAttributes redirectAttributes) {
        UUID registrationId = getRegistrationIdFromSession(session);
        if (registrationId == null) {
            return "redirect:/registration/email-password/email";
        }
        
        try {
            registrationService.sendVerificationCode(registrationId);
            redirectAttributes.addFlashAttribute("message", "Verification code sent successfully!");
            return "redirect:/registration/email-password/verification";
            
        } catch (Exception e) {
            log.error("Error resending verification code", e);
            redirectAttributes.addFlashAttribute("error", "Failed to resend verification code. Please try again.");
            return "redirect:/registration/email-password/verification";
        }
    }
    
    /**
     * Profile step
     */
    @GetMapping("/profile")
    public String showProfileStep(Model model, HttpSession session) {
        UUID registrationId = getRegistrationIdFromSession(session);
        if (registrationId == null) {
            return "redirect:/registration/email-password/email";
        }
        
        model.addAttribute("registrationDto", EmailPasswordRegistrationDto.builder().build());
        return "registration/email-password/profile";
    }
    
    @PostMapping("/profile")
    public String processProfileStep(@Valid @ModelAttribute("registrationDto") EmailPasswordRegistrationDto dto,
                                     BindingResult result,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        
        UUID registrationId = getRegistrationIdFromSession(session);
        if (registrationId == null) {
            return "redirect:/registration/email-password/email";
        }
        
        if (result.hasFieldErrors("name") || result.hasFieldErrors("birthDate")) {
            return "registration/email-password/profile";
        }
        
        try {
            registrationService.updateWithUserProfile(
                    registrationId, 
                    dto.getName(), 
                    dto.getBirthDate(), 
                    dto.getAvatarUrl()
            );
            
            return "redirect:/registration/email-password/complete";
            
        } catch (Exception e) {
            log.error("Error processing profile step", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred. Please try again.");
            return "redirect:/registration/email-password/profile";
        }
    }
    
    /**
     * Complete registration
     */
    @GetMapping("/complete")
    public String showCompleteStep(Model model, HttpSession session) {
        UUID registrationId = getRegistrationIdFromSession(session);
        if (registrationId == null) {
            return "redirect:/registration/email-password/email";
        }
        
        try {
            EmailPasswordRegistration registration = registrationService.getRegistration(registrationId);
            model.addAttribute("email", registration.getEmail());
            model.addAttribute("name", registration.getName());
            return "registration/email-password/complete";
            
        } catch (Exception e) {
            log.error("Error showing complete step", e);
            return "redirect:/registration/email-password/email";
        }
    }
    
    @PostMapping("/complete")
    public String completeRegistration(HttpSession session, RedirectAttributes redirectAttributes) {
        UUID registrationId = getRegistrationIdFromSession(session);
        if (registrationId == null) {
            return "redirect:/registration/email-password/email";
        }
        
        try {
            registrationService.completeRegistration(registrationId);
            session.removeAttribute(REGISTRATION_ID_KEY);
            
            redirectAttributes.addFlashAttribute("message", "Registration completed successfully! You can now log in.");
            return "redirect:/oauth2/authorize/login";
            
        } catch (Exception e) {
            log.error("Error completing registration", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred during registration completion. Please try again.");
            return "redirect:/registration/email-password/complete";
        }
    }

    @PostMapping("/cancel")
    public String cancelRegistration(HttpSession session, RedirectAttributes redirectAttributes) {
        UUID registrationId = getRegistrationIdFromSession(session);
        if (registrationId != null) {
            try {
                registrationService.cleanupRegistration(registrationId);
                session.removeAttribute(REGISTRATION_ID_KEY);
            } catch (Exception e) {
                log.error("Error canceling registration", e);
            }
        }
        
        redirectAttributes.addFlashAttribute("message", "Registration canceled.");
        return "redirect:/oauth2/authorize/login";
    }
    
    private UUID getRegistrationIdFromSession(HttpSession session) {
        Object registrationId = session.getAttribute(REGISTRATION_ID_KEY);
        if (registrationId instanceof UUID) {
            return (UUID) registrationId;
        }
        return null;
    }
}
