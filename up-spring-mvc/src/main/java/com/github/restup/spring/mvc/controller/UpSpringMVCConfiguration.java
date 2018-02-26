package com.github.restup.spring.mvc.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.restup.controller.ResourceController;
import com.github.restup.registry.ResourceRegistry;

/**
 * {@link Configuration} bean defining the {@link SpringMVCResourceController}
 */
@Configuration
public class UpSpringMVCConfiguration {

    @Bean
    public SpringMVCResourceController upSpringMVCResourceController(ResourceRegistry registry, ResourceController controller) {
        return new SpringMVCResourceController(registry, controller);
    }

}
