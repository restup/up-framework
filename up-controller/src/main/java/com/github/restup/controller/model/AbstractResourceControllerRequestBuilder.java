package com.github.restup.controller.model;

import static com.github.restup.service.registry.DiscoveryService.UP_RESOURCE_DISCOVERY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.bind.converter.ParameterConverter;
import com.github.restup.bind.converter.ParameterConverterFactory;
import com.github.restup.errors.Errors;
import com.github.restup.errors.RequestError;
import com.github.restup.errors.RequestErrorException;
import com.github.restup.errors.StatusCode;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.ResourceRelationship;
import com.github.restup.service.model.ResourceData;
import com.github.restup.util.Assert;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractResourceControllerRequestBuilder<T extends AbstractResourceControllerRequestBuilder<T, R>, R extends ResourceControllerRequest> {

    protected HttpMethod method;
    protected Resource<?, ?> resource;
    protected Resource<?, ?> relationship;
    protected ResourceRelationship<?, ?, ?, ?> resourceRelationship;
    protected List<?> ids;
    protected ResourceData<?> body;
    protected String baseRequestUrl;
    protected String requestPath;
    protected String contentTypeParam;
    private ResourceRegistry registry;

    private static String getPath(String[] path, int current) {
        return current >= 0 && current < path.length ? path[current] : null;
    }

    private static RequestErrorException invalidPath(String requestPath) {
        return RequestError.builder()
                .code("INVALID_PATH")
                .detail("{0} is not a valid path", requestPath)
                .buildException();
    }

    private static RequestErrorException invalidPath(String requestPath, String[] parts) {
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

    private static RequestErrorException invalidPath(String requestPath, String resourceName) {
        return RequestError.builder()
                .code("INVALID_RESOURCE_PATH")
                .detail("{0} is not a valid resource", resourceName)
                .meta("resource", resourceName)
                .status(StatusCode.NOT_FOUND)
                .buildException();
    }

    private static RequestErrorException invalidRelationship(Resource<?, ?> a, Resource<?, ?> b) {
        return RequestError.builder()
                .code("INVALID_RELATIONSHIP")
                .title("Unknown relationship")
                .detail("Unknown relationship between {0} and {1}", a.getName(), b.getName())
                .meta("resource", a.getName())
                .meta("requestedRelationship", b.getName())
                .buildException();
    }

    private static RequestErrorException invalidRelationship(Resource<?, ?> a, Resource<?, ?> b, boolean toMany) {
        return RequestError.builder()
                .code("INVALID_RELATIONSHIP_TYPE")
                .title("Invalid relationship")
                .detail("Relationship between {0} and {1} exists, but not of requested type. Did you mean to request /{2}", a.getName(), b.getName(), toMany ? b.getName() : b.getPluralName())
                .meta("resource", a.getName())
                .meta("requestedRelationship", b.getName())
                .buildException();
    }

    static String getPathFromBasePath(String basePath, String requestPath) {
        String result = requestPath;
        if (basePath != null) {
            int i = requestPath.indexOf(basePath);
            if (i < 0) {
                if (basePath.length() > 1 && basePath.endsWith("/")) {
                    result = getPathFromBasePath(basePath.substring(0, basePath.length() - 1),
                        requestPath);
                }
            } else {
                result = requestPath.substring(i + basePath.length());
            }
        }
        return result.startsWith("/") ? result.substring(1) : result;
    }

    public abstract R build();

    protected T me() {
        return (T) this;
    }

    public T registry(ResourceRegistry registry) {
        this.registry = registry;
        return me();
    }

    public T baseRequestUrl(String baseRequestUrl) {
        this.baseRequestUrl = baseRequestUrl;
        return me();
    }

    public T requestPath(String requestPath) {
        this.requestPath = requestPath;
        return me();
    }

    public T contentTypeParam(String contentTypeParam) {
        this.contentTypeParam = contentTypeParam;
        return me();
    }

    public T body(ResourceData<?> body) {
        this.body = body;
        return me();
    }

    public T method(HttpMethod method) {
        this.method = method;
        return me();
    }

    public T resource(Resource<?, ?> resource) {
        this.resource = resource;
        return me();
    }

    public T relationship(Resource<?, ?> relationship) {
        this.relationship = relationship;
        return me();
    }

    public T resourceRelationship(ResourceRelationship<?, ?, ?, ?> resourceRelationship) {
        this.resourceRelationship = resourceRelationship;
        return me();
    }

    public T ids(Object... ids) {
        return ids(Arrays.asList(ids));
    }

    public T ids(List<?> ids) {
        this.ids = ids;
        return me();
    }

    protected boolean isDiscoveryPath(String requestPath) {
        return StringUtils.isBlank(requestPath)
                || Objects.equals("/", requestPath);
    }

    protected String getContentType(Supplier<String[]> contentTypeFromParamSupplier,
        Supplier<String> contentTypeFromRequestSupplier) {
        if (contentTypeParam != null) {
            String[] arr = contentTypeFromParamSupplier.get();
            if (arr != null) {
                for (String s : arr) {
                    if (isNotBlank(s)) {
                        return s;
                    }
                }
            }
        }
        return contentTypeFromRequestSupplier.get();
    }

    public void parsePath() {
        Assert.notNull(registry, "registry is required");
        String basePath = registry.getSettings().getBasePath();
        String path = getPathFromBasePath(basePath, requestPath);
        if (isDiscoveryPath(path)) {
            resource = registry.getResource(UP_RESOURCE_DISCOVERY);
        } else {

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

            resourceRelationship(resourceRelationship);
            if (relationship != null) {
                resource(relationship)
                    .relationship(resource);
            } else {
                resource(resource);
            }

            if (idString != null) {
                Type type = resource.getIdentityField().getType();
                String[] ids = idString.split(",");
                List<Object> list = new ArrayList<>(ids.length);
                ParameterConverterFactory f = registry.getSettings().getParameterConverterFactory();
                ParameterConverter<String, ?> converter = f.getConverter(type);
                Errors errors = null;
                for (String s : ids) {
                    list.add(converter.convert("ids", s, errors));
                }
                ids(list);
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