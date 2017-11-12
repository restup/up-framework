package com.github.restup.spring.mvc.controller;

import com.github.restup.controller.ResourceController;
import com.github.restup.registry.ResourceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link Configuration} bean defining the {@link SpringMVCResourceController}
 */
@Configuration
public class UpSpringMVCConfiguration {

    @Autowired
    @Bean
    public SpringMVCResourceController upSpringMVCResourceController(ResourceRegistry registry, ResourceController controller) {
        return new SpringMVCResourceController(registry, controller);
    }

}
