package com.github.restup.service;

import com.model.test.company.Company;
import com.github.restup.annotations.field.Param;
import com.github.restup.annotations.filter.*;
import com.github.restup.annotations.operations.*;
import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.errors.ErrorObjectException;
import com.github.restup.errors.Errors;
import com.github.restup.path.ResourcePath;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.ResourceRegistryTest;
import com.github.restup.repository.Repository;
import com.github.restup.resource.operations.*;
import com.github.restup.service.model.request.*;
import com.github.restup.service.model.response.PersistenceResult;
import com.github.restup.service.model.response.ReadResult;
import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FilteredServiceTest {

    @Test
    public void testUnsupported() {
        test("", false, false, false, false, false);
    }

    // test interfaces

    @Test
    @SuppressWarnings({"rawtypes"})
    public void testCreateSupported() {
        test(new CreatableResource() {
            public PersistenceResult create(CreateRequest request) {
                return null;
            }
        }, true, false, false, false, false);
    }

    @Test
    @SuppressWarnings({"rawtypes"})
    public void testFindSupported() {
        test(new ReadableResource() {
            public ReadResult find(ReadRequest request) {
                return null;
            }
        }, false, true, false, false, false);
    }

    @Test
    @SuppressWarnings({"rawtypes"})
    public void testUpdateSupported() {
        test(new UpdatableResource() {
            public PersistenceResult update(UpdateRequest request) {
                return null;
            }
        }, false, false, true, false, false);
    }

    @Test
    @SuppressWarnings({"rawtypes"})
    public void testDeleteSupported() {
        test(new DeletableResource() {
            public PersistenceResult delete(DeleteRequest request) {
                return null;
            }
        }, false, false, false, true, false);
    }

    @Test
    @SuppressWarnings({"rawtypes"})
    public void testListSupported() {
        test(new ListableResource() {
            public ReadResult list(ListRequest request) {
                return null;
            }
        }, false, false, false, false, true);
    }

    // test annotations
    @Test
    public void testCreateAnnotationSupported() {
        test(new Repository() {
            @CreateResource
            public void f() {
            }
        }, true, false, false, false, false);
    }

    @Test
    public void testFindAnnotationSupported() {
        test(new Repository() {
            @ReadResource
            public void f() {
            }
        }, false, true, false, false, false);
    }

    @Test
    public void testUpdateAnnotationSupported() {
        test(new Repository() {
            @UpdateResource
            public void f() {
            }
        }, false, false, true, false, false);
    }

    @Test
    public void testDeleteAnnotationSupported() {
        test(new Repository() {
            @DeleteResource
            public void f() {
            }
        }, false, false, false, true, false);
    }

    @Test
    public void testListAnnotationSupported() {
        test(new Repository() {
            @ListResource
            public void f() {
            }
        }, false, false, false, false, true);
    }

    @Test
    public void testPreListAnnotation() {
        test(new Repository() {
            @ListResource
            public void f(Foo foo) {
                assertNotNull(foo);
            }
        }, false, false, false, false, true);
    }

    @SuppressWarnings({"rawtypes"})
    private ResourceServiceOperations getService(Object repo, Object... filters) {
        Resource<?, ?> resource = Resource.builder(Company.class)
                .registry(ResourceRegistryTest.registry()).repository(repo)
                .excludeDefaultServiceFilters(true)
                .serviceFilters(new ServiceFilter() {
                    @Override
                    public <T, ID extends Serializable> boolean accepts(Resource<T, ID> resource) {
                        return false;
                    }

                    @Validation(path = "name")
                    public void f(Errors errors, ResourcePath path) {
                        throw new UnsupportedOperationException("This will never happend since accepts returns false");
                    }
                }, new ServiceFilter() {

                    @Override
                    public <T, ID extends Serializable> boolean accepts(Resource<T, ID> resource) {
                        return true;
                    }

                    @Validation(path = "name")
                    public void f(Errors errors, ResourcePath path) {
                        assertEquals("name", path.toString());
                    }

                    @Validation(path = "name", required = true)
                    public void g(Errors errors, ResourcePath path) {
                        assertEquals("name", path.toString());
                    }

                    @PreCreateFilter
                    @PreUpdateFilter
                    @PreDeleteFilter
                    @Rank(-100)
                    public Foo pre(Resource resource, ResourceRegistry registry, Bar bar, PersistenceRequest request) {
                        assertNotNull(resource);
                        assertNotNull(registry);
                        // assertNotNull(request);
                        assertEquals("boundValue", bar.getName());
                        return new Foo("a");
                    }

                    @PreListFilter
                    @Rank(-100)
                    public Foo preList(ResourceRegistry registry, Resource resource, ListRequest request) {
                        assertNotNull(resource);
                        assertNotNull(registry);
                        return new Foo("a");
                    }

                    @PreReadFilter
                    @Rank(-100)
                    public Foo preRead(Resource resource, ReadRequest request, ResourceRegistry registry) {
                        assertNotNull(resource);
                        assertNotNull(registry);
                        return new Foo("a");
                    }

                    @PreCreateFilter
                    @PreUpdateFilter
                    @PreDeleteFilter
                    @PreListFilter
                    @PreReadFilter
                    public void bbb(Resource resource, Foo foo, Bar bar, ResourceRegistry registry) {
                        assertNotNull(resource);
                        assertNotNull(registry);
                        assertEquals("State returned by prior chain", "a", foo.getName());
                        assertEquals("boundValue", bar.getName());
                        foo.setName("b");
                    }

                    @PreCreateFilter
                    @PreUpdateFilter
                    @PreDeleteFilter
                    @PreListFilter
                    @PreReadFilter
                    public Foo ccc(ResourceRegistry registry, Foo foo, Resource resource, Bar bar,
                                   FilterChainContext ctx) {
                        assertNotNull(resource);
                        assertNotNull(registry);
                        assertNotNull(ctx);
                        assertEquals("State returned by prior chain", "b", foo.getName());
                        assertEquals("boundValue", bar.getName());
                        return new Foo("c");
                    }

                    @PreCreateFilter
                    @PreUpdateFilter
                    @PreDeleteFilter
                    @PreListFilter
                    @PreReadFilter
                    public void ddd(Foo foo, Resource resource, Bar bar, ResourceRegistry registry) {
                        assertNotNull(resource);
                        assertNotNull(registry);
                        assertEquals("State returned by prior chain", "c", foo.getName());
                        assertEquals("boundValue", bar.getName());
                        foo.setName("d");
                    }

                    @PostCreateFilter
                    @PostUpdateFilter
                    @PostDeleteFilter
                    @PostListFilter
                    @PostReadFilter
                    @Rank(-1)
                    public void zzz(Foo foo, Bar bar, FilterChainContext ctx, Resource resource,
                                    ResourceRegistry registry) {
                        assertNotNull(resource);
                        assertNotNull(registry);
                        assertNotNull(ctx);
                        assertEquals("State returned by prior chain", "d", foo.getName());
                        assertEquals("boundValue", bar.getName());
                        foo.setName("e");
                    }

                    @PostCreateFilter
                    @PostUpdateFilter
                    @PostDeleteFilter
                    @PostListFilter
                    @PostReadFilter
                    public void fff(Foo foo, Resource resource, Bar bar, ResourceRegistry registry) {
                        assertNotNull(resource);
                        assertNotNull(registry);
                        assertEquals("State returned by prior chain", "e", foo.getName());
                        assertEquals("boundValue", bar.getName());
                    }
                }, filters).build();
        return resource.getServiceOperations();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void test(Object repo, boolean create, boolean find, boolean update, boolean delete, boolean list) {
        ResourceServiceOperations service = getService(repo);
        ParameterProvider parameterProvider = mock(ParameterProvider.class);

        when(parameterProvider.getParameterNames()).thenReturn(Arrays.asList("barName"));
        when(parameterProvider.getParameter("barName")).thenReturn(new String[]{"boundValue"});
        try {
            service.create(new BasicCreateRequest(null, null, null, null, parameterProvider));
            assertTrue(create);
        } catch (ErrorObjectException e) {
            assertFalse(create);
        }
        try {
            service.find(new BasicReadRequest(null, null, null, parameterProvider));
            assertTrue(find);
        } catch (ErrorObjectException e) {
            assertFalse(find);
        }
        try {
            service.update(new BasicUpdateRequest(null, null, null, null, null, parameterProvider));
            assertTrue(update);
        } catch (ErrorObjectException e) {
            assertFalse(update);
        }
        try {
            service.delete(new BasicDeleteRequest(null, null, null, parameterProvider));
            assertTrue(delete);
        } catch (ErrorObjectException e) {
            assertFalse(delete);
        }
        try {
            service.list(new BasicListRequest(null, null, parameterProvider));
            assertTrue(list);
        } catch (ErrorObjectException e) {
            assertFalse(e.getMessage(), list);
        }

        int occurances = create || update || delete || list || find ? 1 : 0;
        verify(parameterProvider, times(occurances)).getParameter(any(String.class));
    }

    public final static class Foo {
        private String _name;

        public Foo(String name) {
            super();
            this.setName(name);
        }

        public String getName() {
            return _name;
        }

        public void setName(String _name) {
            this._name = _name;
        }

    }

    public final static class Bar {
        private String _name;

        public String getName() {
            return _name;
        }

        @Param("barName")
        public void setName(String _name) {
            this._name = _name;
        }

    }

}
