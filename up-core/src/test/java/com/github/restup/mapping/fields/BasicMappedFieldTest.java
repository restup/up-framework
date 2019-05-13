package com.github.restup.mapping.fields;

import static com.github.restup.mapping.fields.MappedField.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.github.restup.test.assertions.Assertions;
import com.model.test.company.Person;
import com.test.model.animals.Zoo;
import java.util.Collections;
import java.util.HashMap;
import org.junit.Test;

public class BasicMappedFieldTest {
    
    
    /**
     * Builds a Person foo field of a Map
     * 
     * @return builder
     */
    private MappedField.Builder<Person> person() {
        return builder(Person.class)
                .apiName("foo")
                .persistedName("foo")
                .beanName("foo");
    }
    
    @Test
    public void testNewInstance() {
        Person p = person()
                .build().newInstance();
        assertNotNull(p);
    }
    
    @Test
    public void testDeclaringInstance() {
        Object p = person()
                .build().createDeclaringInstance();
        assertNotNull(p);
        assertThat(p, instanceOf(HashMap.class));
    }

    
    @Test
    public void testIsDeclaredBy() throws NoSuchMethodException, SecurityException {
        assertFalse(person().build().isDeclaredBy(Zoo.class));
        assertFalse(builder(Person.class)
                .setter(Person.class.getMethod("setFirstName", String.class))
                .transientField(true)
                .build()
                .isDeclaredBy(Zoo.class));
        BasicMappedField<?> field = new BasicMappedField<>(null, null, null, null, null,
            Collections.emptySet(), false,
            false, false, false, null, null, null, null, null, null);
        assertFalse(field.isDeclaredBy(String.class));
    }
    
    @Test
    public void testHashCodeEquals() {
        Assertions.assertHashCodeEquals(BasicMappedField.class,"beanName", "reader", "writer");
    }
    
    @Test
    public void testIs() {
    }

}
