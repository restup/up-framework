package com.github.restup.registry;

import com.model.test.company.*;
import com.github.restup.mapping.fields.visitors.IdentityByConventionMappedFieldBuilderVisitor;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.repository.collections.MapBackedRepositoryFactory;
import org.junit.Test;

import static com.github.restup.mapping.MappedClassFactoryTest.*;

public class ResourceRegistryTest {

    public static ResourceRegistry registry() {
        return new ResourceRegistry(
                RegistrySettings.builder()
                        .repositoryFactory(new MapBackedRepositoryFactory())
                        .mappedFieldBuilderVisitors(new IdentityByConventionMappedFieldBuilderVisitor())
        );
    }

    @Test
    public void testCompanyGraph() {
        ResourceRegistry registry = registry();
        registry.registerResource(Company.class);
        assertCompany(registry.getMappedClass(Company.class));
        assertPerson(registry.getMappedClass(Person.class));
        assertAddress(registry.getMappedClass(Address.class));
        assertEmployee(registry.getMappedClass(Employee.class));
        assertContractor(registry.getMappedClass(Contractor.class));

        Resource<Company, ?> resource = registry.getResource(Company.class);
        assertCompany(resource.getMapping());
    }

}
