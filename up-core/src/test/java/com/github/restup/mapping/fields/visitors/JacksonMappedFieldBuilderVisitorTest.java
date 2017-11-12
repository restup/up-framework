package com.github.restup.mapping.fields.visitors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.restup.mapping.fields.MappedField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.restup.util.ReflectionUtils.BeanInfo;
import static com.github.restup.util.ReflectionUtils.getBeanInfo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("rawtypes")
public class JacksonMappedFieldBuilderVisitorTest {

    @Test
    public void testAnnotation() {
        MappedField.Builder builder = test(Bar.class);
        assertEquals("nombre", builder.getApiName());
    }

    @Test
    public void testNoAnnotation() {
        MappedField.Builder builder = test(Foo.class);
        assertNull(builder.getApiName());
    }

    @SuppressWarnings("unchecked")
    private MappedField.Builder test(Class<?> clazz) {
        MappedField.Builder builder = MappedField.builder(clazz);
        JacksonMappedFieldBuilderVisitor jackson = new JacksonMappedFieldBuilderVisitor();
        BeanInfo bi = getBeanInfo(clazz);
        jackson.visit(builder, null, bi.getPropertyDescriptor("name"));
        return builder;
    }


    private final class Foo {
        @SuppressWarnings("unused")
        private String name;
    }

    private final class Bar {
        @JsonProperty("nombre")
        private String name;
    }
}
