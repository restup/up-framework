package com.github.restup.spring.boot.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.config.ConfigurationContext;
import com.github.restup.controller.ExceptionHandler;
import com.github.restup.controller.ResourceController;
import com.github.restup.controller.ResourceControllerBuilderDecorator;
import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.content.negotiation.ContentNegotiatorBuilderDecorator;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.CachedServiceDiscovery;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.controller.request.parser.RequestParserBuilderDecorator;
import com.github.restup.mapping.fields.MappedFieldBuilderDecoratorBuilderDecorator;
import com.github.restup.mapping.fields.decorators.JacksonMappedFieldBuilderDecorator;
import com.github.restup.registry.ResourceRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({ResourceController.class})
@AutoConfigureAfter(UpAutoConfiguration.class)
public class UpControllerAutoConfiguration {

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
    public MappedFieldBuilderDecoratorBuilderDecorator jacksonMappedFieldBuilderDecoratorBuilderDecorator() {
        return (b) -> b.add(new JacksonMappedFieldBuilderDecorator());
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceControllerBuilderDecorator defaultUpResourceControllerBuilderDecorator() {
        return (a, b) -> b;
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestParserBuilderDecorator defaultUpRequestParserBuilderDecorator() {
        return (a, b) -> b;
    }

    @Bean
    @ConditionalOnMissingBean
    public ContentNegotiatorBuilderDecorator defaultUpContentNegotiatorBuilderDecorator() {
        return (a, b) -> b;
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestParser.Builder defaultUpRequestParser(ObjectMapper mapper,
        RequestParserBuilderDecorator decorator, ConfigurationContext configurationContext) {
        return RequestParser.builder()
            .decorate(decorator)
            .configurationContext(configurationContext)
            .jacksonObjectMapper(mapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ContentNegotiator.Builder defaultUpContentNegotiator(ServiceDiscovery serviceDiscovery,
        LinkBuilderFactory linkBuilderFactory, ContentNegotiatorBuilderDecorator decorator,
        ConfigurationContext configurationContext) {
        return ContentNegotiator.builder()
            .decorate(decorator)
            .configurationContext(configurationContext)
            .serviceDiscovery(serviceDiscovery)
            .linkBuilderFactory(linkBuilderFactory);
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
        ContentNegotiator.Builder contentNegotiator, ConfigurationContext configurationContext) {
        return ResourceController.builder()
            .decorate(decorator)
            .configurationContext(configurationContext)
            .registry(registry)
            .serviceDiscovery(serviceDiscovery)
            .linkBuilderFactory(linkBuilderFactory)
            .exceptionHandler(exceptionHandler)
            .requestParser(requestParser)
            .contentNegotiator(contentNegotiator)
            .jacksonObjectMapper(mapper)
            // .interceptors(interceptorA, interceptorB, new NoOpRequestInterceptor())
            .build();
    }

}
