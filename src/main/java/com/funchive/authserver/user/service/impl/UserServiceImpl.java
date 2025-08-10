package com.funchive.authserver.user.service.impl;

import com.funchive.authserver.user.exception.UserWithEmailNotFoundException;
import com.funchive.authserver.user.exception.UserWithIdNotFoundException;
import com.funchive.authserver.user.exception.UserWithSlugNotFoundException;
import com.funchive.authserver.user.model.dto.UserCreateDto;
import com.funchive.authserver.user.model.dto.UserDetailDto;
import com.funchive.authserver.user.model.dto.UserUpdateDto;
import com.funchive.authserver.user.model.entity.User;
import com.funchive.authserver.user.model.mapper.UserMapper;
import com.funchive.authserver.user.repository.UserRepository;
import com.funchive.authserver.user.service.UserService;
import com.funchive.authserver.user.service.UserSlugService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final UserSlugService userSlugService;

    @Override
    @Transactional
    public UserDetailDto createUser(UserCreateDto userCreateDto) {
        User user = userMapper.toUser(userCreateDto);

        if (user.getEmail() != null) {
            user.setSlug(userSlugService.generateSlug(user.getName(), user.getEmail()));
        } else {
            user.setSlug(userSlugService.generateSlug(user.getName()));
        }

        User savedUser = userRepository.save(user);
        return userMapper.toUserDetailDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailDto getUserDetailById(UUID id) {
        User user = findUserById(id);
        return userMapper.toUserDetailDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailDto getUserDetailBySlug(String slug) {
        User user = findUserBySlug(slug);
        return userMapper.toUserDetailDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailDto getUserDetailByEmail(String email) {
        User user = findUserByEmail(email);
        return userMapper.toUserDetailDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserDetailDto updateUser(UUID userId, UserUpdateDto userUpdateDto) {
        User user = findUserById(userId);
        userMapper.updateUser(userUpdateDto, user);
        User savedUser = userRepository.save(user);
        return userMapper.toUserDetailDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserWithIdNotFoundException(id));
    }

    private User findUserBySlug(String slug) {
        return userRepository.findBySlug(slug)
                .orElseThrow(() -> new UserWithSlugNotFoundException(slug));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserWithEmailNotFoundException(email));
    }

}
