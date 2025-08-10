package com.funchive.authserver.user.service.impl;

import com.funchive.authserver.user.repository.UserRepository;
import com.funchive.authserver.user.service.UserSlugService;
import com.github.slugify.Slugify;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@AllArgsConstructor
public class UserSlugServiceImpl implements UserSlugService {

    private final UserRepository userRepository;

    private final Slugify slugify = Slugify.builder().lowerCase(true).build();

    private final SecureRandom random = new SecureRandom();

    @Transactional(readOnly = true)
    public String generateSlug(@NonNull String name) {
        String baseSlug = slugify.slugify(name);
        String hashPart = randomHash();

        String slug = baseSlug + "-" + hashPart;

        while (userRepository.existsBySlug(slug)) {
            hashPart = randomHash();
            slug = baseSlug + "-" + hashPart;
        }

        return slug;
    }

    @Transactional(readOnly = true)
    public String generateSlug(@NonNull String name, @NonNull String email) {
        String baseSlug = slugify.slugify(name);
        byte[] emailBytes = email.getBytes(StandardCharsets.UTF_8);

        String hashPart = DigestUtils.md5DigestAsHex(emailBytes).substring(0, 6);
        String slug = baseSlug + "-" + hashPart;

        while (userRepository.existsBySlug(slug)) {
            hashPart = randomHash();
            slug = baseSlug + "-" + hashPart;
        }

        return slug;
    }

    private String randomHash() {
        byte[] randomBytes = new byte[4];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes)
                .substring(0, 6);
    }

}
