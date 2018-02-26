package com.github.restup.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.restup.controller.ResourceController;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.spring.mvc.controller.SpringMVCResourceController;

@Configuration
@ConditionalOnClass(SpringMVCResourceController.class)
@AutoConfigureAfter(UpControllerAutoConfiguration.class)
public class UpSpringMVCControllerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SpringMVCResourceController defaultUpSpringMVCResourceController(ResourceRegistry registry, ResourceController controller) {
        return new SpringMVCResourceController(registry, controller);
    }

}
