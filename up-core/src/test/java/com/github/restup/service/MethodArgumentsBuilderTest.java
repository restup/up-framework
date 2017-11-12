package com.github.restup.service;

import com.model.test.company.Address;
import com.model.test.company.Company;
import com.model.test.company.Employee;
import com.model.test.company.Person;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MethodArgumentsBuilderTest {

    @Test
    public void testBuildArguments() throws NoSuchMethodException, SecurityException {
        MethodArgumentsBuilder b = new MethodArgumentsBuilder();
        b.addArguments(Resource.class, ResourceRegistry.class);
        b.addArguments(MethodArgumentsBuilderTest.class.getMethod("foo", Person.class, Company.class));
        b.addArguments(MethodArgumentsBuilderTest.class.getMethod("bar", Employee.class, Company.class));
        Class<?>[] types = b.build();
        assertEquals(5, types.length);
        assertEquals(Resource.class, types[0]);
        assertEquals(ResourceRegistry.class, types[1]);
        assertEquals(Person.class, types[2]);
        assertEquals(Company.class, types[3]);
        assertEquals(Address.class, types[4]);

        b.addArguments(Resource.class, ResourceRegistry.class);
        b.addArguments(MethodArgumentsBuilderTest.class.getMethod("bar", Employee.class, Company.class));
        b.addArguments(MethodArgumentsBuilderTest.class.getMethod("foo", Person.class, Company.class));

        types = b.build();
        assertEquals(5, types.length);
        assertEquals(Resource.class, types[0]);
        assertEquals(ResourceRegistry.class, types[1]);
        assertEquals(Person.class, types[2]);
        assertEquals(Company.class, types[3]);
        assertEquals(Address.class, types[4]);
    }


    public void foo(Person p, Company c) {
        // test Person/Employee used as same arg
        // & void return type ignored
    }

    public Address bar(Employee e, Company c) {
        // test Person/Employee used as same arg
        // & Address return type added
        return null;
    }

}
