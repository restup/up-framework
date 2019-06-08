package com.github.restup.mapping.fields.decorators;

import static com.github.restup.util.ReflectionUtils.getBeanInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedField.Builder;
import com.github.restup.util.ReflectionUtils.BeanInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JacksonMappedFieldBuilderDecoratorTest {

    @Test
    public void testAnnotation() {
        Builder builder = test(Bar.class, "name");
        assertEquals("nombre", builder.getApiName());
    }

    @Test
    public void testJsonIgnore() {
        Builder builder = test(Bar.class, "description");
        assertNull(builder.getApiName());
    }

    @Test
    public void testNoAnnotation() {
        Builder builder = test(Foo.class, "name");
        assertNull(builder.getApiName());
    }

    private Builder test(Class<?> clazz, String propertyName) {
        Builder builder = MappedField.builder(clazz);
        JacksonMappedFieldBuilderDecorator jackson = new JacksonMappedFieldBuilderDecorator();
        BeanInfo bi = getBeanInfo(clazz);
        jackson.decorate(builder, null, bi.getPropertyDescriptor(propertyName));
        return builder;
    }

    private final class Foo {

        private String name;
    }

    private final class Bar {

        @JsonProperty("nombre")
        private String name;

        @JsonIgnore
        private String description;
    }
}
