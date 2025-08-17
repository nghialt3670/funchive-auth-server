//package com.funchive.authserver.auth.config;
//
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.JavaMailSenderImpl;
//
//import java.util.Properties;
//
//@Configuration
//@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "true")
//public class EmailConfig {
//
//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//
//        // These will be overridden by application.yml properties
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.debug", "false");
//        props.put("mail.smtp.timeout", "10000");
//        props.put("mail.smtp.connectiontimeout", "10000");
//
//        return mailSender;
//    }
//}
