package com.github.restup.bind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.github.restup.bind.converter.ParameterConverterFactory;
import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.errors.Errors;
import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassRegistry;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.service.FilterChainContext;
import com.model.test.company.Person;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMethodArgumentFactoryTest {

    @Mock
    FilterChainContext ctx;
    @Mock
    Errors errors;
    @Mock
    MappedClassRegistry mappedClassRegistry;
    @Mock
    ParameterConverterFactory parameterConverterFactory;
    
    DefaultMethodArgumentFactory factory;
    
    @Before
    public void before() {
        factory = new DefaultMethodArgumentFactory(mappedClassRegistry, parameterConverterFactory);
    }
    
    @After
    public void after() {
        verifyNoMoreInteractions(ctx, errors, mappedClassRegistry, parameterConverterFactory);
    }
    
    private void verifyParameterProvider() {
        verify(ctx).getParameterProvider();
    }
    
    @Test
    public void testNullParameterProvider() {
        Person p = factory.newInstance(Person.class, ctx, null);
        assertNotNull(p);
        verifyParameterProvider();
    }
    
    private ParameterProvider mockNullMappedClass() {
        ParameterProvider parameterProvider = mock(ParameterProvider.class);
        when(ctx.getParameterProvider()).thenReturn(parameterProvider);
        return parameterProvider;
    }
    
    private void verifyNullMappedClass(Class<?> clazz) {
        verify(ctx).getParameterProvider();
        verify(mappedClassRegistry).getMappedClass(clazz);
        verifyParameterProvider();
    }

    @Test
    public void testNullMappedClass() {
        mockNullMappedClass();
        
        Person p = factory.newInstance(Person.class, ctx, null);
        assertNotNull(p);
        verifyNullMappedClass(Person.class);
    }
    
    @SuppressWarnings("unchecked")
    private <T> MappedClass<T> mockNullMappedClassAttributes(Class<T> clazz, List<MappedField<?>> attributes) {
        mockNullMappedClass();
        MappedClass<T> mappedClass = mock(MappedClass.class);
        when(mappedClassRegistry.getMappedClass(clazz)).thenReturn(mappedClass);
        when(mappedClass.getAttributes()).thenReturn(attributes);
        return mappedClass;
    }
    
    private void verifyNullMappedClassAttributes(Class<?> clazz) {
        verifyNullMappedClass(clazz);
        verify(mappedClassRegistry).getMappedClass(clazz);
    }

    @Test
    public void testNullMappedClassAttributes() {
        mockNullMappedClassAttributes(Person.class, null);

        Person p = factory.newInstance(Person.class, ctx, null);
        assertNotNull(p);
        verifyNullMappedClassAttributes(Person.class);
    }

    @Test
    public void testEmptyMappedClassAttributes() {
        mockNullMappedClassAttributes(Person.class, Collections.emptyList());
        
        Person p = factory.newInstance(Person.class, ctx, null);
        assertNotNull(p);
        verifyNullMappedClassAttributes(Person.class);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGetApplicableCollectionInstance() {
        List<Object> list = new ArrayList<>();
        MappedField<Object> field = mock(MappedField.class);
        Collection<Object> result = factory.getApplicableCollectionInstance((MappedField<?>)field, list);
        assertEquals(list, result);
        
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGetApplicableCollectionInstanceWithNullCollectionArg() {
        MappedField<Object> field = mock(MappedField.class);
        
        Object set = new HashSet<>();
        when(field.isCollection()).thenReturn(true);
        when(field.newInstance()).thenReturn(set);
        Collection<Object> result = factory.getApplicableCollectionInstance((MappedField<?>)field, null);
        assertEquals(set, result);
        
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testConvert() {
        MappedField<Object> field = mock(MappedField.class);
        Object result = factory.convert("foo", field, errors, "bar");
        assertEquals("bar", result);
        
        verify(parameterConverterFactory).getConverter(null);
    }

}
