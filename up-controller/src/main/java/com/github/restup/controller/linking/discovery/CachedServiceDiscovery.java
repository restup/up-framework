package com.github.restup.controller.linking.discovery;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;

/**
 * A simple Map backed cache of discovered service urls.
 */
public class CachedServiceDiscovery implements ServiceDiscovery {

    private final Map<Resource<?,?>, String> cache;
    private final ServiceDiscovery delegate;

    public CachedServiceDiscovery(ServiceDiscovery delegate, Map<Resource<?,?>, String> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    public static ServiceDiscovery cache(ServiceDiscovery serviceDiscovery) {
        return new CachedServiceDiscovery(serviceDiscovery);
    }

    CachedServiceDiscovery(ServiceDiscovery delegate) {
        this(delegate, new ConcurrentHashMap<>());
    }

    @Override
    public String locateResourceUrl(ParsedResourceControllerRequest<?> request, Resource<?, ?> resource) {
        String url = cache.get(resource);
        if (url == null) {
            url = delegate.locateResourceUrl(request, resource);
            cache.put(resource, url);
        }
        return url;
    }

}
