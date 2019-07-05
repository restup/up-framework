package com.github.restup.spring.boot.autoconfigure;

import com.github.restup.config.ConfigurationContext;
import com.github.restup.controller.ResourceController;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.spring.mvc.controller.SpringMVCResourceController;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;

@Configuration
@ConditionalOnClass({HandlerMapping.class, SpringMVCResourceController.class})
@AutoConfigureAfter(UpControllerAutoConfiguration.class)
public class UpSpringMVCControllerAutoConfiguration {

    private final ConfigurationContext configurationContext;

    public UpSpringMVCControllerAutoConfiguration(ConfigurationContext configurationContext) {
        super();
        this.configurationContext = configurationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringMVCResourceController defaultUpSpringMVCResourceController(
        ResourceRegistry registry,
        ResourceController controller) {
        return new SpringMVCResourceController(configurationContext, registry, controller);
    }

    @Bean
    public HandlerMapping upControllerUrlHandlerMapping(SpringMVCResourceController controller) throws NoSuchMethodException, SecurityException {
        return SpringMVCResourceController.getHandlerMapping(controller,
            configurationContext.getProperty(ConfigurationContext.ASYNC_CONTROLLER, true));
    }

}
