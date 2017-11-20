package com.github.restup.service.registry;

import com.github.restup.annotations.operations.AutoWrapDisabled;
import com.github.restup.annotations.operations.ListResource;
import com.github.restup.controller.linking.Link;
import com.github.restup.controller.linking.LinkBuilder;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.LinksResult;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * provides list of resources and their collection endpoint as a means for discovering
 * hosted services
 */
public class DiscoveryService {

    // unique resource name, not exposed
    public final static String UP_RESOURCE_DISCOVERY = "Up!ResourceDiscovery";

    private final LinkBuilderFactory factory;

    public DiscoveryService(LinkBuilderFactory factory) {
        this.factory = factory;
    }

    @ListResource
    @AutoWrapDisabled
    public LinksResult listServicesForDiscovery(ParsedResourceControllerRequest<?> request, ResourceRegistry registry) {
        List<Link> result = new ArrayList<>();

        Collection<Resource<?,?>> resources = getResources(registry);
        for ( Resource resource : resources ) {
            if ( ! ignore(resource) ) {
                // get and add all non-ignored collection endpoints
                LinkBuilder builder = factory.getLinkBuilder(request, resource);
                result.add(builder.getCollectionEndpoint(request, resource));
            }
        }
        return new LinksResult(result);
    }

    /**
     *
     * @param resource
     * @return Ignores and resource whose type is the internal {@link Resource}
     */
    protected boolean ignore(Resource resource) {
        return resource.getType() == Resource.class;
    }

    protected Collection<Resource<?,?>> getResources(ResourceRegistry registry) {
        return registry.getResources();
    }

}
