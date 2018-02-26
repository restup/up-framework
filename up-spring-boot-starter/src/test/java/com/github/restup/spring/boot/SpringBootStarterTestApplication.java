package com.github.restup.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"com.university"})
public class SpringBootStarterTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootStarterTestApplication.class, args);
    }

}
