package com.github.restup.controller.settings;

import static com.github.restup.service.registry.DiscoveryService.UP_RESOURCE_DISCOVERY;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.ExceptionHandler;
import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.interceptor.NoOpRequestInterceptor;
import com.github.restup.controller.interceptor.RequestInterceptor;
import com.github.restup.controller.interceptor.RequestInterceptorChain;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.request.parser.RequestParser;
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

    ExceptionHandler getExceptionHandler();

    String getMediaTypeParam();

    class Builder {

        private ResourceRegistry registry;
        private ContentNegotiator contentNegotiator;
        private RequestInterceptor[] interceptors;
        private RequestParser requestParser;
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
            return contentNegotiator(contentNegotiator.capture(settingsCaptor).build());
        }

        public Builder interceptors(RequestInterceptor... interceptors) {
            this.interceptors = interceptors;
            return me();
        }

        public Builder requestParser(RequestParser.Builder requestParser) {
            return requestParser(requestParser.capture(settingsCaptor).build());
        }

        public Builder requestParser(RequestParser requestParser) {
            this.requestParser = requestParser;
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
            settingsCaptor.build();

            RequestInterceptor interceptor = getInterceptor(interceptors);
            RequestParser requestParser = this.requestParser;
            if (requestParser == null) {
                requestParser = RequestParser.builder()
                        .capture(settingsCaptor)
                        .jacksonObjectMapper(mapper)
                        .build();
            }
            ContentNegotiator contentNegotiator = this.contentNegotiator;
            if ( contentNegotiator == null ) {
                contentNegotiator = ContentNegotiator.builder()
                        .capture(settingsCaptor)
                        .build();
            }
            ExceptionHandler exceptionHandler = this.exceptionHandler;
            if (exceptionHandler == null) {
                exceptionHandler = ExceptionHandler.getDefaultInstance();
            }

            //TODO
            registry.registerResource(
                    Resource.builder(Resource.class)
                            .service(new DiscoveryService(settingsCaptor.getLinkBuilderFactory()))
                            .excludeFrameworkFilters(true)
                            .mapping(
                                    MappedClass.builder(Resource.class)
                                            .name(UP_RESOURCE_DISCOVERY)
                                            .pluralName("resources")
                                            .id(String.class, "name")
                            )
            );
            return new BasicControllerSettings(registry, contentNegotiator, interceptor,
                requestParser, exceptionHandler, settingsCaptor.getDefaultMediaType(),
                mediaTypeParam);
        }
    }

}
