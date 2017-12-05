package com.github.restup.controller.linking.discovery;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;

/**
 * Default implementation builds urls based upon request & registry details
 */
public class DefaultServiceDiscovery implements ServiceDiscovery {

    public String locateResourceUrl(ParsedResourceControllerRequest<?> request, Resource<?, ?> resource) {
        StringBuilder sb = new StringBuilder();
        String base = request.getBaseRequestUrl();
        sb.append(base);
        sb.append(resource.getBasePath());
        sb.append(resource.getPluralName());
        return sb.toString();
    }

}
