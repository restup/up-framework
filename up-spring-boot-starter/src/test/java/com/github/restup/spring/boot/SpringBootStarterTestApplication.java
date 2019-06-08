package com.github.restup.spring.boot;

import com.github.restup.controller.ResourceControllerBuilderDecorator;
import com.github.restup.controller.model.MediaType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EntityScan(basePackages = {"com.university"})
public class SpringBootStarterTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootStarterTestApplication.class, args);
    }

    @Bean
    public ResourceControllerBuilderDecorator resourceControllerBuilderDecorator() {
        return (b) -> b
            .defaultMediaType(MediaType.APPLICATION_JSON_API)
            .mediaTypeParam("mediaType");
    }

}
