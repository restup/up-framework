package com.github.restup.controller.linking;

import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.model.ParsedResourceControllerRequest;

/**
 * Default factory for providing {@link LinkBuilder} implementation, provides {@link DefaultLinkBuilder} implementation.
 */
public class DefaultLinkBuilderFactory implements LinkBuilderFactory {

    private final LinkBuilder linkBuilder;

    public DefaultLinkBuilderFactory(ServiceDiscovery serviceDiscovery) {
        this.linkBuilder = new DefaultLinkBuilder(serviceDiscovery);
    }

    @Override
    public LinkBuilder getLinkBuilder(ParsedResourceControllerRequest<?> request, Object result) {
        return linkBuilder;
    }

}
