package com.github.restup.spring.boot.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerMapping;
import com.github.restup.controller.ResourceController;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.spring.mvc.controller.SpringMVCResourceController;

@Configuration
@ConditionalOnClass({HandlerMapping.class, SpringMVCResourceController.class})
@AutoConfigureAfter(UpControllerAutoConfiguration.class)
@EnableConfigurationProperties(UpProperties.class)
public class UpSpringMVCControllerAutoConfiguration {

    private final UpProperties props;

    public UpSpringMVCControllerAutoConfiguration(UpProperties props) {
        super();
        this.props = props;
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringMVCResourceController defaultUpSpringMVCResourceController(ResourceRegistry registry, ResourceController controller) {
        return new SpringMVCResourceController(props.getBasePath(), registry, controller);
    }

    @Bean
    public HandlerMapping upControllerUrlHandlerMapping(SpringMVCResourceController controller) throws NoSuchMethodException, SecurityException {
        return SpringMVCResourceController.getHandlerMapping(controller, props.isAsyncController());
    }

}
