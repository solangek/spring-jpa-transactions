package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point.
 *
 * @SpringBootApplication enables component scanning (so our @Controller, @Service
 * and repository are picked up) and Spring Boot auto-configuration (web server,
 * DataSource, JPA/Hibernate, Thymeleaf - all wired up from application.properties).
 */
@SpringBootApplication
public class SpringJpaTransactions {

    public static void main(String[] args) {
        SpringApplication.run(SpringJpaTransactions.class, args);
    }

}
