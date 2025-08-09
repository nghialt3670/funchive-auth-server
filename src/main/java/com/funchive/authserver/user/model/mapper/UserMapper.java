package com.funchive.authserver.user.model.mapper;

import com.funchive.authserver.user.model.dto.UserCreateDto;
import com.funchive.authserver.user.model.dto.UserDetailDto;
import com.funchive.authserver.user.model.dto.UserUpdateDto;
import com.funchive.authserver.user.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {
    User toUser(UserCreateDto userDto);

    void updateUser(UserUpdateDto userDto, @MappingTarget User user);

    UserDetailDto toUserDetailDto(User user);
}
