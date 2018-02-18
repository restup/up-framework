package com.github.restup.controller.settings;

import static com.github.restup.service.registry.DiscoveryService.UP_RESOURCE_DISCOVERY;
import org.apache.commons.lang3.ArrayUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.ExceptionHandler;
import com.github.restup.controller.content.negotiation.ContentNegotiator;
import com.github.restup.controller.content.negotiation.DefaultContentNegotiator;
import com.github.restup.controller.content.negotiation.JsonApiContentNegotiator;
import com.github.restup.controller.content.negotiation.JsonContentNegotiator;
import com.github.restup.controller.interceptor.NoOpRequestInterceptor;
import com.github.restup.controller.interceptor.RequestInterceptor;
import com.github.restup.controller.interceptor.RequestInterceptorChain;
import com.github.restup.controller.linking.DefaultLinkBuilderFactory;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.CachedServiceDiscovery;
import com.github.restup.controller.linking.discovery.DefaultServiceDiscovery;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.request.parser.DefaultRelationshipsParser;
import com.github.restup.controller.request.parser.ParameterParserChain;
import com.github.restup.controller.request.parser.RequestParamParser;
import com.github.restup.controller.request.parser.RequestParser;
import com.github.restup.controller.request.parser.RequestParserChain;
import com.github.restup.jackson.JacksonConfiguration;
import com.github.restup.mapping.MappedClass;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.settings.AutoDetectConstants;
import com.github.restup.service.registry.DiscoveryService;
import com.google.gson.Gson;

public interface ControllerSettings {


    static Builder builder() {
        return new Builder();
    }

    String getDefaultMediaType();

    ResourceRegistry getRegistry();

    ContentNegotiator[] getContentNegotiators();

    RequestInterceptor getRequestInterceptor();

    RequestParser getRequestParser();

    ExceptionHandler getExceptionHandler();

    static class Builder {

        private ResourceRegistry registry;
        private ContentNegotiator[] contentNegotiator;
        private RequestInterceptor[] interceptors;
        private RequestParser[] requestParsers;
        private RequestParamParser[] requestParamParsers;
        private RequestParser relationshipParser;
        private ExceptionHandler exceptionHandler;
        private ObjectMapper mapper;
        private Gson gson;
        private boolean autoDetectDisabled = false;
        private LinkBuilderFactory linkBuilderFactory;
        private ServiceDiscovery serviceDiscovery;
        private String defaultMediaType;
        
        private Builder() {
            super();
        }

        private static RequestParser getRequestParser(RequestParser[] requestParsers, RequestParamParser[] requestParamParsers, RequestParser relationshipParser) {
            RequestParser[] arr = requestParsers;
            ParameterParserChain paramParser = getRequestParamParser(requestParamParsers);
            int size = ArrayUtils.getLength(arr);
            if (size == 0) {
                return new RequestParserChain(paramParser, relationshipParser);
            }
            return new RequestParserChain(ArrayUtils.addAll(arr, paramParser, relationshipParser));
        }

        private static ParameterParserChain getRequestParamParser(RequestParamParser[] requestParamParsers) {
            RequestParamParser[] arr = requestParamParsers;
            int size = ArrayUtils.getLength(arr);
            if (size == 0) {
                // with defaults
                return new ParameterParserChain();
            }
            return new ParameterParserChain(arr);
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

        private ContentNegotiator[] getContentNegotiator(ContentNegotiator[] contentNegotiators) {
            ContentNegotiator[] arr = contentNegotiators;
            if (!autoDetectDisabled) {
                ServiceDiscovery discovery = serviceDiscovery;
                LinkBuilderFactory factory = linkBuilderFactory;
                if (discovery == null) {
                    discovery = new DefaultServiceDiscovery();
                    discovery = new CachedServiceDiscovery(discovery);
                }
                if (factory == null) {
                    factory = new DefaultLinkBuilderFactory(discovery);
                }
                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    arr = ArrayUtils.addAll(arr, new JsonContentNegotiator()
                            , new JsonApiContentNegotiator(factory));
                }
                linkBuilderFactory = factory;
            }
            if (defaultMediaType != null) {
                DefaultContentNegotiator defaultContentNegotiator = new DefaultContentNegotiator(defaultMediaType, arr);
                arr = ArrayUtils.add(arr, defaultContentNegotiator);
            }
            if (arr == null) {
                return new ContentNegotiator[]{};
            }
            return arr;
        }

        private Builder me() {
            return this;
        }

        public Builder registry(ResourceRegistry registry) {
            this.registry = registry;
            return me();
        }

        public Builder contentNegotiators(ContentNegotiator... contentNegotiator) {
            this.contentNegotiator = contentNegotiator;
            return me();
        }

        public Builder interceptors(RequestInterceptor... interceptors) {
            this.interceptors = interceptors;
            return me();
        }

        public Builder requestParsers(RequestParser... requestParsers) {
            this.requestParsers = requestParsers;
            return me();
        }

        public Builder relationshipParser(RequestParser relationshipParser) {
            this.relationshipParser = relationshipParser;
            return me();
        }

        public Builder requestParamParsers(RequestParamParser... requestParamParsers) {
            this.requestParamParsers = requestParamParsers;
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
            this.serviceDiscovery = serviceDiscovery;
            return me();
        }

        public Builder linkBuilderFactory(LinkBuilderFactory linkBuilderFactory) {
            this.linkBuilderFactory = linkBuilderFactory;
            return me();
        }

        public Builder gson(Gson gson) {
            this.gson = gson;
            return me();
        }

        public Builder autoDetectDisabled(boolean autoDetectDisabled) {
            this.autoDetectDisabled = autoDetectDisabled;
            return me();
        }

        public Builder defaultMediaType(String mediaType) {
            this.defaultMediaType = mediaType;
            return me();
        }

        public ControllerSettings build() {

            if (!autoDetectDisabled) {
                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    requestParsers(JacksonConfiguration.parser(mapper, defaultMediaType));
                }
            }
            RequestParser relationshipsParser = this.relationshipParser;
            if (relationshipsParser == null) {
                relationshipsParser = new DefaultRelationshipsParser();
            }
            RequestInterceptor interceptor = getInterceptor(this.interceptors);
            RequestParser requestParser = getRequestParser(requestParsers, requestParamParsers, relationshipsParser);
            ContentNegotiator[] contentNegotiators = getContentNegotiator(this.contentNegotiator);
            ExceptionHandler exceptionHandler = this.exceptionHandler;
            if (exceptionHandler == null) {
                exceptionHandler = ExceptionHandler.getDefaultInstance();
            }

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
            return new BasicControllerSettings(registry, contentNegotiators, interceptor, requestParser, exceptionHandler, defaultMediaType);
        }
    }

}
