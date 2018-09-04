package com.github.restup.spring.boot.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.ResourceControllerBuilderDecorator;
import com.github.restup.controller.ExceptionHandler;
import com.github.restup.controller.ResourceController;
import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.CachedServiceDiscovery;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.registry.ResourceRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ResourceController.class})
@EnableConfigurationProperties(UpProperties.class)
@AutoConfigureAfter(UpAutoConfiguration.class)
public class UpControllerAutoConfiguration {

    private final UpProperties props;

    public UpControllerAutoConfiguration(UpProperties props) {
        super();
        this.props = props;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceDiscovery defaultUpServiceDiscovery() {
        return CachedServiceDiscovery
                .cache(ServiceDiscovery.getDefaultServiceDiscovery());
    }

    @Bean
    @ConditionalOnMissingBean
    public LinkBuilderFactory defaultUpLinkBuilderFactory(ServiceDiscovery serviceDiscovery) {
        return LinkBuilderFactory.getDefaultLinkBuilderFactory(serviceDiscovery);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionHandler defaultUpExceptionHandler() {
        return ExceptionHandler.getDefaultInstance();
    }

    @Bean
    @ConditionalOnMissingBean(value = {RequestParser.class, ObjectMapper.class})
    public ObjectMapper defaultUpObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceControllerBuilderDecorator defaultUpResourceControllerBuilderDecorator() {
        return (b) -> b;
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestParser defaultUpRequestParser(ObjectMapper mapper) {
        return RequestParser.builder()
                .autoDetectDisabled(props.isDisableSerializationAutoDetection())
                .defaultMediaType(props.getDefaultMediaType())
                .fieldsParamName(props.getFieldsParamName())
                .filterParamName(props.getFilterParamName())
                .includeParamName(props.getIncludeParamName())
                .pageLimitParamName(props.getLimitParamName())
                .pageNumberParamName(props.getPageNumberParamName())
                .pageOffsetParamName(props.getOffsetParamName())
                .sortParamName(props.getSortParamName())
                .jacksonObjectMapper(mapper)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ContentNegotiator defaultUpContentNegotiator(ServiceDiscovery serviceDiscovery,
            LinkBuilderFactory linkBuilderFactory) {
        return ContentNegotiator.builder()
                .autoDetectDisabled(props.isDisableSerializationAutoDetection())
                .defaultMediaType(props.getDefaultMediaType())
                .serviceDiscovery(serviceDiscovery)
                .linkBuilderFactory(linkBuilderFactory)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceController defaultUpResourceController(ResourceRegistry registry,
            ServiceDiscovery serviceDiscovery,
            LinkBuilderFactory linkBuilderFactory,
            ExceptionHandler exceptionHandler,
            RequestParser requestParser,
        ResourceControllerBuilderDecorator decorator,
            ContentNegotiator contentNegotiator) {
        return decorator.decorate(ResourceController.builder()
                .registry(registry)
                .serviceDiscovery(serviceDiscovery)
                .linkBuilderFactory(linkBuilderFactory)
                .exceptionHandler(exceptionHandler)
                .autoDetectDisabled(props.isDisableSerializationAutoDetection())
                .defaultMediaType(props.getDefaultMediaType())
                .requestParser(requestParser)
                .contentNegotiator(contentNegotiator)
                // .interceptors(interceptorA, interceptorB, new NoOpRequestInterceptor())
        ).build();
    }

}
