package com.github.restup.registry;

import static com.github.restup.mapping.MappedClassFactoryTest.assertAddress;
import static com.github.restup.mapping.MappedClassFactoryTest.assertCompany;
import static com.github.restup.mapping.MappedClassFactoryTest.assertContractor;
import static com.github.restup.mapping.MappedClassFactoryTest.assertEmployee;
import static com.github.restup.mapping.MappedClassFactoryTest.assertPerson;

import com.github.restup.mapping.fields.visitors.IdentityByConventionMappedFieldBuilderVisitor;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.repository.collections.MapBackedRepositoryFactory;
import com.model.test.company.Address;
import com.model.test.company.Company;
import com.model.test.company.Contractor;
import com.model.test.company.Employee;
import com.model.test.company.Person;
import org.junit.Test;

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
