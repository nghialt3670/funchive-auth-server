package com.funchive.authserver.auth.service;

import com.funchive.authserver.auth.model.dto.AuthorityDetailDto;

public interface AuthorityService {

    AuthorityDetailDto getAuthorityDetail(String name);

}
