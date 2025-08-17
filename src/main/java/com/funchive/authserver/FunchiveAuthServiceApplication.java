package com.funchive.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class FunchiveAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunchiveAuthServiceApplication.class, args);
    }

}
