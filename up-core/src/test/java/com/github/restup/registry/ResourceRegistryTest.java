package com.github.restup.registry;

import static com.github.restup.mapping.MappedClassFactoryTest.assertAddress;
import static com.github.restup.mapping.MappedClassFactoryTest.assertCompany;
import static com.github.restup.mapping.MappedClassFactoryTest.assertContractor;
import static com.github.restup.mapping.MappedClassFactoryTest.assertEmployee;
import static com.github.restup.mapping.MappedClassFactoryTest.assertPerson;
import static com.github.restup.util.TestRegistries.mapBackedRegistry;
import static com.github.restup.util.TestRegistries.universityRegistry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import java.util.Comparator;
import org.junit.Test;
import com.github.restup.bind.MethodArgumentFactory;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.mapping.MappedClassFactory;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderVisitor;
import com.github.restup.mapping.fields.MappedFieldFactory;
import com.github.restup.path.ResourcePathsProvider;
import com.github.restup.query.Pagination;
import com.github.restup.registry.settings.ControllerMethodAccess;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.registry.settings.ServiceMethodAccess;
import com.github.restup.repository.RepositoryFactory;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.model.test.company.Address;
import com.model.test.company.Company;
import com.model.test.company.Contractor;
import com.model.test.company.Employee;
import com.model.test.company.Person;
import com.university.University;

public class ResourceRegistryTest {

    @Test
    public void testHasResource() {
        ResourceRegistry registry = universityRegistry();
        assertTrue(registry.hasResource(University.class));
        assertFalse(registry.hasResource(Company.class));

        assertTrue(registry.hasResource("university"));
        assertFalse(registry.hasResource("foo"));
    }

    @Test
    public void testGetResourceByPluralName() {
        ResourceRegistry registry = universityRegistry();
        assertEquals("university", registry.getResourceByPluralName("universities").getName());
        assertNull(registry.getResourceByPluralName("foo"));
    }

    @Test
    public void testCompanyGraph() {
        ResourceRegistry registry = mapBackedRegistry();
        registry.registerResource(Company.class);
        assertCompany(registry.getMappedClass(Company.class));
        assertPerson(registry.getMappedClass(Person.class));
        assertAddress(registry.getMappedClass(Address.class));
        assertEmployee(registry.getMappedClass(Employee.class));
        assertContractor(registry.getMappedClass(Contractor.class));

        Resource<Company, ?> resource = registry.getResource(Company.class);
        assertCompany(resource.getMapping());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testBuilder() {
        ControllerMethodAccess controllerMethodAccess = mock(ControllerMethodAccess.class);
        ResourcePathsProvider restrictedFieldsProvider = mock(ResourcePathsProvider.class);
        ResourcePathsProvider defaultSparseFieldsProvider = mock(ResourcePathsProvider.class);
        ErrorFactory errorFactory = mock(ErrorFactory.class);
        MappedClassFactory mappedClassFactory = mock(MappedClassFactory.class);
        MappedFieldBuilderVisitor visitor = mock(MappedFieldBuilderVisitor.class);
        Comparator<MappedField<?>> mappedFieldOrderComparator = mock(Comparator.class);
        MethodArgumentFactory methodArgumentFactory = mock(MethodArgumentFactory.class);
        RepositoryFactory repositoryFactory = mock(RepositoryFactory.class);
        ServiceMethodAccess serviceMethodAccess = mock(ServiceMethodAccess.class);
        MappedFieldFactory mappedFieldFactory = mock(MappedFieldFactory.class);
        RequestObjectFactory requestObjectFactory = mock(RequestObjectFactory.class);
        ResourceRegistryRepository resourceRegistryMap = mock(ResourceRegistryRepository.class);

        ResourceRegistry registry = ResourceRegistry.builder()
            .basePath("/api")
            .controllerMethodAccess(controllerMethodAccess)
            .defaultPaginationDisabled()
            .defaultPagination(10)
            .defaultPagination(10, 10, false)
            .defaultPagination(Pagination.of(20, 0))
            .defaultRestrictedFieldsProvider(restrictedFieldsProvider)
            .defaultServiceFilters("")
            .defaultSparseFieldsProvider(defaultSparseFieldsProvider)
            .errorFactory(errorFactory)
            .excludeFrameworkFilters(true)
            .mappedClassFactory(mappedClassFactory)
            .mappedFieldBuilderVisitors(visitor)
            .mappedFieldFactory(mappedFieldFactory)
            .mappedFieldOrderComparator(mappedFieldOrderComparator)
            .methodArgumentFactory(methodArgumentFactory)
            .packagesToScan("com.foo","com.bar")
            .repositoryFactory(repositoryFactory)
            .requestObjectFactory(requestObjectFactory)
            .resourceRegistryRepository(resourceRegistryMap)
            .serviceMethodAccess(serviceMethodAccess)
            .build();
            
        RegistrySettings settings = registry.getSettings();
        
        assertSame(controllerMethodAccess, settings.getDefaultControllerAccess());
        assertSame(restrictedFieldsProvider, settings.getDefaultRestrictedFieldsProvider());
        assertSame(defaultSparseFieldsProvider, settings.getDefaultSparseFieldsProvider());
        assertSame(errorFactory, settings.getErrorFactory());
        assertSame(mappedClassFactory, settings.getMappedClassFactory());
        assertSame(mappedFieldOrderComparator, settings.getMappedFieldOrderComparator());
        assertSame(methodArgumentFactory, settings.getMethodArgumentFactory());
        assertSame(repositoryFactory, settings.getRepositoryFactory());
        assertSame(serviceMethodAccess, settings.getDefaultServiceAccess());
        assertSame(mappedFieldFactory, settings.getMappedFieldFactory());
        assertSame(requestObjectFactory, settings.getRequestObjectFactory());
    }

}
