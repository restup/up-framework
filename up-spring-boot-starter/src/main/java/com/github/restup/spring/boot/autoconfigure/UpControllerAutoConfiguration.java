package com.github.restup.spring.boot.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.ResourceControllerBuilderDecorator;
import com.github.restup.controller.ExceptionHandler;
import com.github.restup.controller.ResourceController;
import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.content.negotiation.ContentNegotiatorBuilderDecorator;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.CachedServiceDiscovery;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.request.parser.RequestParamParser;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.controller.request.parser.RequestParserBuilderDecorator;
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
    public RequestParserBuilderDecorator defaultUpRequestParserBuilderDecorator() {
        return (b) -> b;
    }

    @Bean
    @ConditionalOnMissingBean
    public ContentNegotiatorBuilderDecorator defaultUpContentNegotiatorBuilderDecorator() {
        return (b) -> b;
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestParser.Builder defaultUpRequestParser(ObjectMapper mapper,
        RequestParserBuilderDecorator decorator) {
        return decorator.decorate(
            RequestParser.builder()
                .autoDetectDisabled(props.isDisableSerializationAutoDetection())
                .defaultMediaType(props.getDefaultMediaType())
                .requestParamParser(RequestParamParser.builder()
                    .withFieldsNamed(props.getFieldsParamName())
                    .withFilterNamed(props.getFilterParamName())
                    .withIncludeNamed(props.getIncludeParamName())
                    .withPageLimitNamed(props.getLimitParamName())
                    .withPageNumberNamed(props.getPageNumberParamName())
                    .withPageOffsetNamed(props.getOffsetParamName())
                    .withSortNamed(props.getSortParamName())
                )
                .jacksonObjectMapper(mapper)
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ContentNegotiator.Builder defaultUpContentNegotiator(ServiceDiscovery serviceDiscovery,
        LinkBuilderFactory linkBuilderFactory, ContentNegotiatorBuilderDecorator decorator) {
        return decorator.decorate(
            ContentNegotiator.builder()
                .autoDetectDisabled(props.isDisableSerializationAutoDetection())
                .defaultMediaType(props.getDefaultMediaType())
                .serviceDiscovery(serviceDiscovery)
                .linkBuilderFactory(linkBuilderFactory)
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceController defaultUpResourceController(ResourceRegistry registry,
        ObjectMapper mapper,
            ServiceDiscovery serviceDiscovery,
            LinkBuilderFactory linkBuilderFactory,
            ExceptionHandler exceptionHandler,
        RequestParser.Builder requestParser,
        ResourceControllerBuilderDecorator decorator,
        ContentNegotiator.Builder contentNegotiator) {
        return decorator.decorate(
            ResourceController.builder()
                .registry(registry)
                .serviceDiscovery(serviceDiscovery)
                .linkBuilderFactory(linkBuilderFactory)
                .exceptionHandler(exceptionHandler)
                .autoDetectDisabled(props.isDisableSerializationAutoDetection())
                .defaultMediaType(props.getDefaultMediaType())
                .requestParser(requestParser)
                .contentNegotiator(contentNegotiator)
                .jacksonObjectMapper(mapper)
                // .interceptors(interceptorA, interceptorB, new NoOpRequestInterceptor())
        ).build();
    }

}
