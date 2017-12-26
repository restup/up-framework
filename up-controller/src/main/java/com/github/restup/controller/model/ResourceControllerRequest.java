package com.github.restup.controller.model;

import static com.github.restup.service.registry.DiscoveryService.UP_RESOURCE_DISCOVERY;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.bind.converter.ParameterConverter;
import com.github.restup.bind.converter.ParameterConverterFactory;
import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.ErrorObjectException;
import com.github.restup.errors.Errors;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.ResourceRelationship;
import com.github.restup.service.model.ResourceData;
import com.github.restup.util.Assert;

/**
 * In an http request, this is a partially parsed details from the request, having parsed the request path to obtain resource info and ids.
 */
public abstract class ResourceControllerRequest implements ParameterProvider {

    private final HttpMethod method;
    private final Resource<?, ?> resource;
    private final Resource<?, ?> relationship;
    private final ResourceRelationship<?, ?, ?, ?> resourceRelationship;
    private final ResourceData<?> body;
    private final List<?> ids;
    private final String contentType;
    private final String baseRequestUrl;
    private final String requestUrl;

    protected ResourceControllerRequest(HttpMethod method, Resource<?, ?> resource, List<?> ids, Resource<?, ?> relationship, ResourceRelationship<?, ?, ?, ?> resourceRelationship, ResourceData<?> body
            , String contentType, String baseRequestUrl, String requestUrl) {
        this.resource = resource;
        this.ids = ids;
        this.relationship = relationship;
        this.resourceRelationship = resourceRelationship;
        this.method = method;
        this.body = body;
        this.contentType = contentType;
        this.baseRequestUrl = baseRequestUrl;
        this.requestUrl = requestUrl;
    }

    public Enumeration<String> getHeaders(String name) {
        return null;
    }

    public String getContentType() {
        return contentType;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Resource<?, ?> getResource() {
        return resource;
    }

    public Resource<?, ?> getRelationship() {
        return relationship;
    }

    public ResourceRelationship<?, ?, ?, ?> getResourceRelationship() {
        return resourceRelationship;
    }

    public List<?> getIds() {
        return ids;
    }

    public ResourceData<?> getBody() {
        return body;
    }

    public String getBaseRequestUrl() {
        return baseRequestUrl;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public abstract static class AbstractBuilder<T extends AbstractBuilder<T, R>, R extends ResourceControllerRequest> {

        protected HttpMethod method;
        protected Resource<?, ?> resource;
        protected Resource<?, ?> relationship;
        protected ResourceRelationship<?, ?, ?, ?> resourceRelationship;
        protected List<?> ids;
        protected ResourceData<?> body;
        protected String baseRequestUrl;
        protected String requestPath;
        private ResourceRegistry registry;

        private static String getPath(String[] path, int current) {
            return current >= 0 && current < path.length ? path[current] : null;
        }

        private static ErrorObjectException invalidPath(String requestPath) {
            return ErrorBuilder.builder()
                    .code("INVALID_PATH")
                    .detail("{0} is not a valid path", requestPath)
                    .buildException();
        }

        private static ErrorObjectException invalidPath(String requestPath, String[] parts) {
            int n = 0;
            if (parts.length > 3) {
                n = parts.length - 3;
            }
            String resourceName = getPath(parts, n);
            if (StringUtils.isBlank(resourceName)) {
                resourceName = getPath(parts, n + 1);
            }
            return invalidPath(requestPath, resourceName);
        }

        private static ErrorObjectException invalidPath(String requestPath, String resourceName) {
            return ErrorBuilder.builder()
                    .code("INVALID_RESOURCE_PATH")
                    .detail("{0} is not a valid resource", resourceName)
                    .meta("resource", resourceName)
                    .status(ErrorBuilder.ErrorCodeStatus.NOT_FOUND)
                    .buildException();
        }

        private static ErrorObjectException invalidRelationship(Resource<?, ?> a, Resource<?, ?> b) {
            return ErrorBuilder.builder()
                    .code("INVALID_RELATIONSHIP")
                    .title("Unknown relationship")
                    .detail("Unknown relationship between {0} and {1}", a.getName(), b.getName())
                    .meta("resource", a.getName())
                    .meta("requestedRelationship", b.getName())
                    .buildException();
        }

        private static ErrorObjectException invalidRelationship(Resource<?, ?> a, Resource<?, ?> b, boolean toMany) {
            return ErrorBuilder.builder()
                    .code("INVALID_RELATIONSHIP_TYPE")
                    .title("Invalid relationship")
                    .detail("Relationship between {0} and {1} exists, but not of requested type. Did you mean to request /{2}", a.getName(), b.getName(), toMany ? b.getName() : b.getPluralName())
                    .meta("resource", a.getName())
                    .meta("requestedRelationship", b.getName())
                    .buildException();
        }

        public abstract R build();

        @SuppressWarnings("unchecked")
        protected T me() {
            return (T) this;
        }

        public T setRegistry(ResourceRegistry registry) {
            this.registry = registry;
            return me();
        }

        public T setBaseRequestUrl(String baseRequestUrl) {
            this.baseRequestUrl = baseRequestUrl;
            return me();
        }

        public T setRequestPath(String requestPath) {
            this.requestPath = requestPath;
            return me();
        }

        public T setBody(ResourceData<?> body) {
            this.body = body;
            return me();
        }

        public T setMethod(HttpMethod method) {
            this.method = method;
            return me();
        }

        public T setResource(Resource<?, ?> resource) {
            this.resource = resource;
            return me();
        }

        public T setRelationship(Resource<?, ?> relationship) {
            this.relationship = relationship;
            return me();
        }

        public T setResourceRelationship(ResourceRelationship<?, ?, ?, ?> resourceRelationship) {
            this.resourceRelationship = resourceRelationship;
            return me();
        }

        public T setIds(Object... ids) {
            return setIds(Arrays.asList(ids));
        }

        public T setIds(List<?> ids) {
            this.ids = ids;
            return me();
        }

        protected boolean isDiscoveryPath(String requestPath) {
            return StringUtils.isBlank(requestPath)
                    || Objects.equals("/", requestPath);
        }

        public void parsePath() {
            Assert.notNull(registry, "registry is required");
            if (isDiscoveryPath(requestPath)) {
                resource = registry.getResource(UP_RESOURCE_DISCOVERY);
            } else {
                String path = null;
                String basePath = registry.getSettings().getBasePath();
                if (basePath != null) {
                    int i = requestPath.indexOf(basePath);
                    path = i < 0 ? requestPath : requestPath.substring(i+basePath.length());
                } else {
                    path = requestPath;
                }

                // get path portion (/foos/123) less base path (/context/foos/123)

                // TODO extension based content negotiation? .json, .hal, .jsonapi(?)

                // get path parts
                String arr[] = path.split("/");
                int n = arr.length - 1;
                int resourcePathSize = 0;
                // ex 1. foos          ; list all foos
                // ex 2. foos/ids      ; Get by id
                // ex 3. foos/ids/bars ; get a to many relationship
                // ex 4. foos/ids/bar  ; get a to one relationship
                // ex 5. foos/resourceActions ; execute a resource action //TODO
                // ex 6. foos/ids/bars/ids/bazs ; get deep relationship //TODO

                // foos must be *plural* resource name. bar might be plural or singular.
                // foos is most likely (?) to be at n because of POST and GET (n) operations
                // foos is also likely (?) to be at n-1 because of GET (1), PATCH, DELETE and list operations
                String pathZ = getPath(arr, n);
                String pathY = getPath(arr, n - 1);

                Resource<?, ?> resource = registry.getResourceByPluralName(pathY);

                Resource<?, ?> relationship = null;
                ResourceRelationship<?, ?, ?, ?> resourceRelationship = null;

                String idString = null;
                if (resource != null) {
                    // if there is a resource at pathY, we know that we have request ex 2.
                    // TODO or ex 5.
                    idString = pathZ;
                    resourcePathSize = 2;
                } else {
                    String pathX = getPath(arr, n - 2);
                    // otherwise, we have to check if it is a relationship
                    resource = registry.getResourceByPluralName(pathX);
                    if (resource == null) {
                        // if there is not a resource at pathX know we have ex1
                        resource = registry.getResourceByPluralName(pathZ);
                        resourcePathSize = 1;
                    } else if (n >= 2) {
                        resourcePathSize = 3;
                        // otherwise we have a relationship
                        idString = pathY;
                        boolean toMany = false;
                        relationship = registry.getResource(pathZ);
                        if (relationship == null) {
                            relationship = registry.getResourceByPluralName(pathZ);
                            if (relationship == null) {
                                throw invalidPath(requestPath, pathZ);
                            }
                            toMany = true;
                        }
                        resourceRelationship = registry.getRelationship(resource.getName(), relationship.getName());
                        if (resourceRelationship == null) {
                            throw invalidRelationship(resource, relationship);
                        }
                        RelationshipType type = resourceRelationship.getType(resource);
                        if (toMany != RelationshipType.isToMany(type)) {
                            throw invalidRelationship(resource, relationship, toMany);
                        }
                    }
                }

                if (resource == null) {
                    throw invalidPath(requestPath, arr);
                }

                setResourceRelationship(resourceRelationship);
                if (relationship != null) {
                    setResource(relationship)
                            .setRelationship(resource);
                } else {
                    setResource(resource);
                }

                if (idString != null) {
                    Type type = resource.getIdentityField().getType();
                    String[] ids = idString.split(",");
                    List<Object> list = new ArrayList<Object>(ids.length);
                    ParameterConverterFactory f = registry.getSettings().getParameterConverterFactory();
                    ParameterConverter<String, ?> converter = f.getConverter(type);
                    Errors errors = null;
                    for (String s : ids) {
                        list.add(converter.convert("ids", s, errors));
                    }
                    setIds(list);
                }

                if (basePath != null) {
                    // if basePath is not null path is strict, so confirm there are not extra path parts
                    if (arr.length != resourcePathSize) {
                        throw invalidPath(requestPath);
                    }
                }
            }
        }

    }
}
