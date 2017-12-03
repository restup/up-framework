package com.github.restup.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.github.restup.mapping.fields.IterableField;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.registry.ResourceRegistryTest;
import com.model.test.company.Address;
import com.model.test.company.Company;
import com.model.test.company.Contractor;
import com.model.test.company.Employee;
import com.model.test.company.Person;

public class MappedClassFactoryTest {

    MappedClassFactory factory = ResourceRegistryTest.registry();

    public static MappedField<?> assertField(MappedClass<?> mappedClass, int i, String name, Class<?> type) {
        return assertField(mappedClass, i, name, type, false, false, false, null);
    }

    private static MappedField<?> caseInsensitive(MappedClass<?> mappedClass, int i, String name, Class<?> type, String searchField) {
        MappedField<?> field = assertField(mappedClass, i, name, type, true, false, false, null);
        assertEquals(searchField, field.getCaseInsensitiveSearchField());
        return field;
    }

    private static MappedField<?> relationship(MappedClass<?> mappedClass, int i, String name, Class<?> type, Class<?> relationship) {
        return assertField(mappedClass, i, name, type, false, false, false, relationship);
    }

    private static MappedField<?> assertIdField(MappedClass<?> mappedClass, int i, String name, Class<?> type, boolean errorOnUpdate) {
        MappedField<?> f = readOnly(mappedClass, i, name, type, errorOnUpdate);
        assertTrue(f.isIdentifier());
        return f;
    }

    private static MappedField<?> assertIdField(MappedClass<?> mappedClass, int i, String name, Class<?> type) {
        MappedField<?> f = assertField(mappedClass, i, name, type, false, true, false, null);
        assertTrue(f.isIdentifier());
        return f;
    }

    private static MappedField<?> readOnly(MappedClass<?> mappedClass, int i, String name, Class<?> type, boolean errorOnUpdate) {
        return assertField(mappedClass, i, name, type, false, true, errorOnUpdate, null);
    }

    
	private static MappedField<?> assertField(MappedClass<?> mappedClass, int i, String name, Class<?> type, boolean insensitive, boolean readOnly, boolean errorOnUpdate, Class<?> relationshipClass) {
        @SuppressWarnings({ "rawtypes", "unchecked" })
		MappedField<Object> field = (MappedField)mappedClass.getAttributes().get(i);
        assertEquals(name, field.getApiName());
        assertEquals(name, field.getBeanName());
        assertEquals(name, field.getPersistedName());
        assertEquals(name, field.getPersistedName());
        assertEquals(type, field.getType());
        assertEquals(true, field.isApiProperty());
        assertEquals(false, field.isTransientField());
        assertEquals(readOnly, field.isImmutable());
        if ( field.isImmutable() ) {
	        assertEquals("Immutability", errorOnUpdate, field.isImmutabilityErrorOnUpdateAttempt());
	        assertEquals("Immutability", !errorOnUpdate, field.isImmutabilityIgnoreUpdateAttempt());
        }
        assertEquals(insensitive, field.isCaseInsensitive());

        assertEquals(relationshipClass, field.getRelationshipResource());
        assertEquals(relationshipClass == null ? null : "id", field.getRelationshipJoinField());
        if (relationshipClass == null) {
            assertNull(field.getRelationshipResource());
        }
        field.writeValue(null, null);
        assertNull(field.readValue(null));
        try {
            Object o = mappedClass.getType().newInstance();
            field.writeValue(o, null);
            assertNull(field.readValue(null));
            Object value = getValue(field.getType());
            field.writeValue(o, value);
            assertEquals(value, field.readValue(o));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return field;
    }

    private static Object getValue(Class<?> type) {
        if (type.equals(Long.class)) {
            return Long.valueOf(1);
        }
        if (type.equals(String.class)) {
            return "a";
        }
        if (type.equals(List.class)) {
            return Arrays.asList(1, 2);
        }
        if (type.equals(Address.class)) {
            return new Address();
        }
        throw new UnsupportedOperationException(type.getName());
    }

    private static void assertMappedClass(MappedClass<?> mappedClass, Class<?> type, Class<?> parentType, int size) {
        assertMappedClass(mappedClass, type, parentType, type.getName(), size);
    }

    private static void assertMappedClass(MappedClass<?> mappedClass, Class<?> type, Class<?> parentType, String name, int size) {
        assertNotNull("mappedClass", mappedClass);
        assertNotNull("attributes", mappedClass.getAttributes());
        assertEquals("size", size, mappedClass.getAttributes().size());
        assertEquals("name", name, mappedClass.getName());
        assertEquals("type", type, mappedClass.getType());
        assertEquals("parentType", parentType, mappedClass.getParentType());
    }

    public static void assertCompany(MappedClass<Company> mappedClass) {
        assertMappedClass(mappedClass, Company.class, null, "company", 3);
        assertIdField(mappedClass, 0, "id", String.class);
        assertField(mappedClass, 1, "name", String.class);
        collection(relationship(mappedClass, 2, "workers", List.class, Person.class), Long.class);
    }

    @SuppressWarnings("rawtypes")
    private static void collection(MappedField<?> field, Class<?> genericType) {
        assertTrue(field instanceof IterableField);
        IterableField it = (IterableField) field;
        assertEquals(it.getGenericType(), genericType);
    }

    public static void assertPerson(MappedClass<Person> mappedClass) {
        assertMappedClass(mappedClass, Person.class, null, 5);
        assertIdField(mappedClass, 0, "id", Long.class, true);
        assertField(mappedClass, 1, "address", Address.class);
        caseInsensitive(mappedClass, 2, "firstName", String.class, null);
        caseInsensitive(mappedClass, 3, "lastName", String.class, "lastNameLowerCased");
        assertField(mappedClass, 4, "lastNameLowerCased", String.class);
    }

    public static void assertAddress(MappedClass<Address> mappedClass) {
        assertMappedClass(mappedClass, Address.class, null, 1);
        assertField(mappedClass, 0, "street", String.class);
    }

    public static void assertContractor(MappedClass<Contractor> mappedClass) {
        assertMappedClass(mappedClass, Contractor.class, Person.class, 6);
        assertIdField(mappedClass, 0, "id", Long.class, true);
        assertField(mappedClass, 1, "address", Address.class);
        relationship(mappedClass, 2, "companyId", Long.class, Company.class);
        caseInsensitive(mappedClass, 3, "firstName", String.class, null);
        caseInsensitive(mappedClass, 4, "lastName", String.class, "lastNameLowerCased");
        assertField(mappedClass, 5, "lastNameLowerCased", String.class);
    }

    public static void assertEmployee(MappedClass<Employee> mappedClass) {
        assertMappedClass(mappedClass, Employee.class, Person.class, 6);
        assertIdField(mappedClass, 0, "id", Long.class, false);
        assertField(mappedClass, 1, "address", Address.class);
        assertField(mappedClass, 2, "department", String.class);
        caseInsensitive(mappedClass, 3, "firstName", String.class, null);
        caseInsensitive(mappedClass, 4, "lastName", String.class, "lastNameLowerCased");
        assertField(mappedClass, 5, "lastNameLowerCased", String.class);
    }

    @Test
    public void testCompany() {
        assertCompany(factory.getMappedClass(Company.class));
    }

    @Test
    public void testPerson() {
        assertPerson(factory.getMappedClass(Person.class));
    }

    @Test
    public void testAddress() {
        assertAddress(factory.getMappedClass(Address.class));
    }

    @Test
    public void testContractor() {
        assertContractor(factory.getMappedClass(Contractor.class));
    }

    @Test
    public void testEmployee() {
        assertEmployee(factory.getMappedClass(Employee.class));
    }
}
