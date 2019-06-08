package com.github.restup.aws.lambda.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.ExceptionHandler;
import com.github.restup.controller.ResourceController;
import com.github.restup.controller.ResourceControllerBuilderDecorator;
import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.content.negotiation.ContentNegotiatorBuilderDecorator;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.CachedServiceDiscovery;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.request.parser.RequestParamParser;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.controller.request.parser.RequestParserBuilderDecorator;
import com.github.restup.mapping.fields.MappedFieldBuilderDecoratorBuilderDecorator;
import com.github.restup.mapping.fields.decorators.JacksonMappedFieldBuilderDecorator;
import com.github.restup.registry.ResourceRegistry;

public class UpControllerConfigurationTemplate {

    private final ResourceRegistry registry;
    private final UpProperties props;
    private final ObjectMapper mapper;

    public UpControllerConfigurationTemplate(ResourceRegistry registry, ObjectMapper mapper,
        UpProperties props) {
        this.registry = registry;
        this.mapper = mapper;
        this.props = props;
    }

    public UpControllerConfigurationTemplate(ResourceRegistry registry, UpProperties props) {
        this(registry, new ObjectMapper(), props);
    }

    public UpControllerConfigurationTemplate(ResourceRegistry registry) {
        this(registry, new UpProperties());
    }

    protected final ObjectMapper getObjectMapper() {
        return mapper;
    }

    protected ServiceDiscovery getServiceDiscovery() {
        return CachedServiceDiscovery
            .cache(ServiceDiscovery.getDefaultServiceDiscovery());
    }

    protected LinkBuilderFactory getLinkBuilderFactory() {
        return LinkBuilderFactory.getDefaultLinkBuilderFactory(getServiceDiscovery());
    }

    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getDefaultInstance();
    }

    protected MappedFieldBuilderDecoratorBuilderDecorator getMappedFieldBuilderDecoratorBuilderDecorator() {
        return (b) -> b.add(new JacksonMappedFieldBuilderDecorator());
    }

    protected ResourceControllerBuilderDecorator getResourceControllerBuilderDecorator() {
        return (b) -> b;
    }

    protected RequestParserBuilderDecorator getRequestParserBuilderDecorator() {
        return (b) -> b;
    }

    protected ContentNegotiatorBuilderDecorator getContentNegotiatorBuilderDecorator() {
        return (b) -> b;
    }

    protected RequestParser.Builder getRequestParser() {
        return getRequestParserBuilderDecorator().decorate(
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

    protected ContentNegotiator.Builder getContentNegotiator() {
        return getContentNegotiatorBuilderDecorator().decorate(
            ContentNegotiator.builder()
                .autoDetectDisabled(props.isDisableSerializationAutoDetection())
                .defaultMediaType(props.getDefaultMediaType())
                .serviceDiscovery(getServiceDiscovery())
                .linkBuilderFactory(getLinkBuilderFactory())
        );
    }

    public ResourceController getResourceController() {
        return getResourceControllerBuilderDecorator().decorate(
            ResourceController.builder()
                .registry(registry)
                .serviceDiscovery(getServiceDiscovery())
                .linkBuilderFactory(getLinkBuilderFactory())
                .exceptionHandler(getExceptionHandler())
                .autoDetectDisabled(props.isDisableSerializationAutoDetection())
                .defaultMediaType(props.getDefaultMediaType())
                .requestParser(getRequestParser())
                .contentNegotiator(getContentNegotiator())
                .jacksonObjectMapper(mapper)
            // .interceptors(interceptorA, interceptorB, new NoOpRequestInterceptor())
        ).build();
    }

}
