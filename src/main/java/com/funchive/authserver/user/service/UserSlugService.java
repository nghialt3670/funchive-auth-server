package com.funchive.authserver.user.service;

public interface UserSlugService {

    String generateSlug(String name);

    String generateSlug(String name, String email);

}
