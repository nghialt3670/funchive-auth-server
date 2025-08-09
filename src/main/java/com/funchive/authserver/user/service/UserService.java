package com.funchive.authserver.user.service;

import com.funchive.authserver.user.model.dto.UserCreateDto;
import com.funchive.authserver.user.model.dto.UserDetailDto;
import com.funchive.authserver.user.model.dto.UserUpdateDto;

import java.util.UUID;

public interface UserService {

    UserDetailDto createUser(UserCreateDto userCreateDto);

    UserDetailDto getUserDetailById(UUID id);

    UserDetailDto getUserDetailBySlug(String slug);

    UserDetailDto getUserDetailByEmail(String email);

    boolean checkEmailExists(String email);

    UserDetailDto updateUser(UUID userId, UserUpdateDto userUpdateDto);

    void deleteUser(UUID userId);

}
