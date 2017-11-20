package com.github.restup.service;

import com.model.test.company.Company;
import com.github.restup.annotations.filter.PreCreateFilter;
import com.github.restup.annotations.filter.Validation;
import com.github.restup.annotations.operations.CreateResource;
import com.github.restup.annotations.operations.ListResource;
import com.github.restup.annotations.operations.UpdateResource;
import com.github.restup.errors.ErrorBuilder;
import com.github.restup.errors.ErrorObjectException;
import com.github.restup.errors.Errors;
import com.github.restup.errors.RequestError;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.ResourceRegistryTest;
import com.github.restup.repository.Repository;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.DefaultRequestObjectFactory;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.github.restup.service.model.request.UpdateRequest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static com.github.restup.errors.ErrorBuilder.ErrorCodeStatus.BAD_REQUEST;
import static com.github.restup.errors.ErrorBuilder.ErrorCodeStatus.INTERNAL_SERVER_ERROR;
import static org.junit.Assert.*;

public class FilteredServiceMethodCommandTest {

    private final static Logger log = LoggerFactory.getLogger(FilteredServiceMethodCommandTest.class);

    private ResourceRegistry registry;

    private static RequestObjectFactory getInstance() {
        return new DefaultRequestObjectFactory();
    }

    @Test(expected = ErrorObjectException.class)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testRuntimeException() {
        final String s = null;
        create(new Repository() {
                   @PreCreateFilter
                   public void f(Errors errors) {
                   }

                   @SuppressWarnings("null")
                   @CreateResource
                   public void f(CreateRequest request) {
                       s.toLowerCase();
                   }
               }
                , new Function<RequestError, Boolean>() {
                    public Boolean apply(RequestError e) {
                        assertFalse(e.getId().isEmpty());
                        UUID.fromString(e.getId());
                        assertEquals(INTERNAL_SERVER_ERROR.getHttpStatus(), e.getHttpStatus());
                        assertEquals(INTERNAL_SERVER_ERROR.name(), e.getCode());
                        assertEquals(INTERNAL_SERVER_ERROR.getDefaultTitle(), e.getTitle());
                        assertEquals(INTERNAL_SERVER_ERROR.getDefaultDetail(), e.getDetail());
                        Map<String, Object> meta = (Map) e.getMeta();
                        assertEquals("company", meta.get("resource"));
                        assertEquals(1, meta.size());
                        assertEquals(null, e.getSource());
                        return null;
                    }
                });
    }

    @Test(expected = ErrorObjectException.class)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testErrorObjectException() {
        create(new Repository() {
                   @Validation(path = "name", required = false)
                   public void e(Errors errors, ResourcePath path) {
                       errors.addError(ErrorBuilder.builder().detail("Skip not required validation"));
                   }

                   @Validation(path = "name")
                   public void f(Errors errors, ResourcePath path) {
                       errors.addError(ErrorBuilder.builder()
                               .detail("A {0} error occurred", "test")
                               .source(path));
                   }

                   @Validation(path = "name", skipOnErrors = true)
                   public void g(Errors errors, ResourcePath path) {
                       errors.addError(ErrorBuilder.builder().detail("Skip this error as f() created an error and skip on errors is true"));
                   }

                   @CreateResource
                   public void f(Company request) {
                   }
               }
                , new Function<RequestError, Boolean>() {
                    public Boolean apply(RequestError e) {
                        assertFalse(e.getId().isEmpty());
                        UUID.fromString(e.getId());
                        assertEquals(BAD_REQUEST.getHttpStatus(), e.getHttpStatus());
                        assertEquals("COMPANY_NAME_ERROR", e.getCode());
                        Map<String, Object> meta = (Map) e.getMeta();
                        assertEquals("company", meta.get("resource"));
                        assertEquals(1, meta.size());
                        assertEquals("/data/name", e.getSource().getSource());
                        assertEquals(BAD_REQUEST.getDefaultTitle(), e.getTitle());
                        assertEquals("A test error occurred", e.getDetail());
                        return null;
                    }
                });
    }

    @Test(expected = ErrorObjectException.class)
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void testErrorObjectExceptionOnUpdate() {
        update(new Repository() {
                   @Validation(path = {"name", "workers"})
                   public void f(Errors errors, ResourcePath path, Company c) {
                       assertNotNull(c.getWorkers());
                       errors.addError(ErrorBuilder.builder()
                               .detail("A {0} error occurred", "test")
                               .source(path));
                   }

                   @Validation(path = "name", skipOnErrors = true)
                   public void g(Errors errors, ResourcePath path) {
                       errors.addError(ErrorBuilder.builder().detail("Skip this error as f() created an error and skip on errors is true"));
                   }

                   @UpdateResource
                   public void f(Company request) {
                   }

                   @ListResource
                   public Company g() {
                       Company c = new Company();
                       c.setWorkers(Arrays.asList(1l));
                       return c;
                   }
               }
                , new Function<RequestError, Boolean>() {
                    public Boolean apply(RequestError e) {
                        assertFalse(e.getId().isEmpty());
                        UUID.fromString(e.getId());
                        assertEquals(BAD_REQUEST.getHttpStatus(), e.getHttpStatus());
                        assertEquals("COMPANY_NAME_ERROR", e.getCode());
                        Map<String, Object> meta = (Map) e.getMeta();
                        assertEquals("company", meta.get("resource"));
                        assertEquals(1, meta.size());
                        assertEquals("/data/name", e.getSource().getSource());
                        assertEquals(BAD_REQUEST.getDefaultTitle(), e.getTitle());
                        assertEquals("A test error occurred", e.getDetail());
                        return null;
                    }
                });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void create(Repository repository, Function<RequestError, Boolean> function) {
        try {
            getService(repository).create((CreateRequest) getInstance().getCreateRequest(null, null, null, null, null));
        } catch (ErrorObjectException e) {
            assertEquals(1, e.getErrors().size());
            function.apply(e.getErrors().iterator().next());
            throw e;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void update(Repository repository, Function<RequestError, Boolean> function) {
        try {
            Company c = new Company();
            c.setName("foo");
            ResourceServiceOperations service = getService(repository);
            List<ResourcePath> paths = Arrays.asList(ResourcePath.path(registry, Company.class, "name"));
            service.update((UpdateRequest) getInstance().getUpdateRequest(null, 1l, c, paths, null, null));
        } catch (ErrorObjectException e) {
            log.debug("Error",e.getCause());
            assertEquals(1, e.getErrors().size());
            function.apply(e.getErrors().iterator().next());
            throw e;
        }
    }


    @SuppressWarnings({"rawtypes"})
    private ResourceServiceOperations getService(Object repo, Object... filters) {
        registry = ResourceRegistryTest.registry();
        Resource<?, ?> resource = Resource.builder(Company.class)
                .registry(registry)
                .repository(repo)
                .serviceFilters(repo)
                .build();
        registry.registerResource(resource);
        return resource.getServiceOperations();
    }
}
