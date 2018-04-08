package com.github.restup.controller.linking;

import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.model.ParsedResourceControllerRequest;

/**
 * A factory for providing {@link LinkBuilder} implementations which may be configured using {@link com.github.restup.controller.settings.ControllerSettings.Builder#linkBuilderFactory(LinkBuilderFactory)}
 */
public interface LinkBuilderFactory {

    static LinkBuilderFactory getDefaultLinkBuilderFactory(ServiceDiscovery discovery) {
        return new DefaultLinkBuilderFactory(discovery);
    }

    /**
     * The resulting {@link LinkBuilder} could be implemented as singleton, thread safe or accept the
     * arguments for specific requests.
     *
     * @param request object
     * @param result object
     *
     * @return a link builder for the request.
     */
    LinkBuilder getLinkBuilder(ParsedResourceControllerRequest<?> request, Object result);

}
