package com.github.restup.bind;

import static org.junit.Assert.*;
import org.junit.Test;
import com.model.test.company.Person;

public class SimpleMethodArgumentFactoryTest {

    @Test
    public void testNullArguments() {
        SimpleMethodArgumentFactory factory = new SimpleMethodArgumentFactory();
        assertNull(factory.newInstance(null, null, null));
    }
    
    @Test
    public void testNewInstance() {
        SimpleMethodArgumentFactory factory = new SimpleMethodArgumentFactory();
        assertNotNull(factory.newInstance(Person.class, null, null));
    }
}
