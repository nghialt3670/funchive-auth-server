package com.funchive.authserver.auth.model.mapper;

import com.funchive.authserver.auth.model.dto.AuthorityDetailDto;
import com.funchive.authserver.auth.model.entity.Authority;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AuthorityMapper {
    AuthorityDetailDto toAuthorityDetailDto(Authority authority);
}
