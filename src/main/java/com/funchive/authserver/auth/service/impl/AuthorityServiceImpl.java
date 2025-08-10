package com.funchive.authserver.auth.service.impl;

import com.funchive.authserver.auth.exception.AuthorityNotFoundException;
import com.funchive.authserver.auth.model.dto.AuthorityDetailDto;
import com.funchive.authserver.auth.model.entity.Authority;
import com.funchive.authserver.auth.model.mapper.AuthorityMapper;
import com.funchive.authserver.auth.repository.AuthorityRepository;
import com.funchive.authserver.auth.service.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    private final AuthorityMapper authorityMapper;

    @Override
    public AuthorityDetailDto createAuthority(String name) {
        Authority authority = new Authority();
        authority.setName(name);
        Authority savedAuthority = authorityRepository.save(authority);
        return authorityMapper.toAuthorityDetailDto(savedAuthority);
    }

    @Override
    public AuthorityDetailDto getAuthorityDetail(String name) {
        Authority authority = authorityRepository.findByName(name)
                .orElseThrow(() -> new AuthorityNotFoundException(name));

        return authorityMapper.toAuthorityDetailDto(authority);
    }

    @Override
    public boolean checkAuthorityExists(String name) {
        return authorityRepository.existsByName(name);
    }

}
