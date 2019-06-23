package com.github.restup.controller.settings;

import static com.github.restup.service.registry.DiscoveryService.UP_RESOURCE_DISCOVERY;
import static com.github.restup.util.UpUtils.nvl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.ExceptionHandler;
import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.interceptor.NoOpRequestInterceptor;
import com.github.restup.controller.interceptor.RequestInterceptor;
import com.github.restup.controller.interceptor.RequestInterceptorChain;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.controller.request.parser.path.DefaultRequestPathParser;
import com.github.restup.controller.request.parser.path.RequestPathParser;
import com.github.restup.mapping.MappedClass;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.registry.DiscoveryService;
import org.apache.commons.lang3.ArrayUtils;

public interface ControllerSettings {


    static Builder builder() {
        return new Builder();
    }

    String getDefaultMediaType();

    ResourceRegistry getRegistry();

    ContentNegotiator getContentNegotiator();

    RequestInterceptor getRequestInterceptor();

    RequestParser getRequestParser();

    RequestPathParser getRequestPathParser();

    ExceptionHandler getExceptionHandler();

    String getMediaTypeParam();

    LinkBuilderFactory getLinkBuilderFactory();

    class Builder {

        private ResourceRegistry registry;
        private ContentNegotiator contentNegotiator;
        private ContentNegotiator.Builder contentNegotiatorBuilder;
        private RequestInterceptor[] interceptors;
        private RequestParser requestParser;
        private RequestPathParser requestPathParser;
        private RequestParser.Builder requestParserBuilder;
        private ExceptionHandler exceptionHandler;
        private ObjectMapper mapper;
        private BuilderSettingsCaptor settingsCaptor;
        private String mediaTypeParam;
        
        private Builder() {
            super();
            settingsCaptor = new BuilderSettingsCaptor();
        }

        private static RequestInterceptor getInterceptor(RequestInterceptor[] interceptors) {
            RequestInterceptor[] arr = interceptors;
            int size = ArrayUtils.getLength(arr);
            if (size == 0) {
                return new NoOpRequestInterceptor();
            } else if (size == 1) {
                return arr[0];
            }
            return new RequestInterceptorChain(arr);
        }

        private Builder me() {
            return this;
        }

        public Builder registry(ResourceRegistry registry) {
            this.registry = registry;
            return me();
        }

        public Builder contentNegotiator(ContentNegotiator contentNegotiator) {
            this.contentNegotiator = contentNegotiator;
            return me();
        }

        public Builder contentNegotiator(ContentNegotiator.Builder contentNegotiator) {
            contentNegotiatorBuilder = contentNegotiator;
            return me();
        }

        public Builder interceptors(RequestInterceptor... interceptors) {
            this.interceptors = interceptors;
            return me();
        }

        public Builder requestParser(RequestParser.Builder requestParser) {
            requestParserBuilder = requestParser;
            return me();
        }

        public Builder requestParser(RequestParser requestParser) {
            this.requestParser = requestParser;
            return me();
        }

        public Builder requestPathParser(RequestPathParser requestPathParser) {
            requestPathParser = requestPathParser;
            return me();
        }

        public Builder exceptionHandler(ExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
            return me();
        }

        public Builder jacksonObjectMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return me();
        }

        public Builder serviceDiscovery(ServiceDiscovery serviceDiscovery) {
            settingsCaptor.setServiceDiscovery(serviceDiscovery);
            return me();
        }

        public Builder linkBuilderFactory(LinkBuilderFactory linkBuilderFactory) {
            settingsCaptor.setLinkBuilderFactory(linkBuilderFactory);
            return me();
        }

        public Builder autoDetectDisabled(boolean autoDetectDisabled) {
            settingsCaptor.setAutoDetectDisabled(autoDetectDisabled);
            return me();
        }

        public Builder defaultMediaType(String mediaType) {
            settingsCaptor.setDefaultMediaType(mediaType);
            return me();
        }

        public Builder mediaTypeParam(String mediaTypeParam) {
            this.mediaTypeParam = mediaTypeParam;
            return me();
        }

        public ControllerSettings build() {
            RequestInterceptor interceptor = getInterceptor(interceptors);

            RequestParser.Builder requestParserBuilder = nvl(this.requestParserBuilder,
                () -> RequestParser.builder())
                .capture(settingsCaptor)
                .jacksonObjectMapper(mapper);

            ContentNegotiator.Builder contentNegotiatorBuilder = nvl(this.contentNegotiatorBuilder,
                () -> ContentNegotiator.builder())
                .capture(settingsCaptor);

            settingsCaptor.build();

            ContentNegotiator contentNegotiator = nvl(this.contentNegotiator,
                () -> contentNegotiatorBuilder.build());
            RequestParser requestParser = nvl(this.requestParser,
                () -> requestParserBuilder.build());
            RequestPathParser requestPathParser = nvl(this.requestPathParser,
                () -> new DefaultRequestPathParser(registry));

            ExceptionHandler exceptionHandler = nvl(this.exceptionHandler,
                () -> ExceptionHandler.getDefaultInstance());

            LinkBuilderFactory linkBuilderFactory = settingsCaptor.getLinkBuilderFactory();

            //TODO
            registry.registerResource(
                    Resource.builder(Resource.class)
                        .service(new DiscoveryService(linkBuilderFactory))
                            .excludeFrameworkFilters(true)
                            .mapping(
                                    MappedClass.builder(Resource.class)
                                            .name(UP_RESOURCE_DISCOVERY)
                                            .pluralName("resources")
                                            .id(String.class, "name")
                            )
            );
            return new BasicControllerSettings(registry, contentNegotiator, interceptor,
                requestParser, requestPathParser, exceptionHandler,
                settingsCaptor.getDefaultMediaType(),
                mediaTypeParam, linkBuilderFactory);
        }
    }

}
