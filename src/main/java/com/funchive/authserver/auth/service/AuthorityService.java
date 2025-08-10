package com.funchive.authserver.auth.service;

import com.funchive.authserver.auth.model.dto.AuthorityDetailDto;

public interface AuthorityService {

    AuthorityDetailDto createAuthority(String name);

    AuthorityDetailDto getAuthorityDetail(String name);

    boolean checkAuthorityExists(String name);

}
