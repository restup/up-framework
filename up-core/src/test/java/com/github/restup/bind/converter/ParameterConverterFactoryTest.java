package com.github.restup.bind.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.test.assertions.Assertions;

@RunWith(MockitoJUnitRunner.class)
public class ParameterConverterFactoryTest {
    
    @Mock
    private ErrorFactory errorFactory;
    
    ParameterConverterFactory getFactory() {
        return ParameterConverterFactory.builder(errorFactory).build();
    }

    @Test
    public void testGetNoOpConverter() {
        ParameterConverter<String,String> converter = getFactory().getConverter(String.class);
        assertEquals("foo", converter.convert("fooParam", "foo", null));
    }

    @Test
    public void testUnsupportedConverter() {
        Assertions.assertThrows(()->getFactory().getConverter(List.class), UnsupportedOperationException.class)
        .hasNoCause();
    }

    @Test
    public void testAddAll() {
        assertNotNull(ParameterConverterFactory.builder(errorFactory).addAll(null).build());
    }

    @Test
    public void testGetConverter() {

        ConverterFactory converterFactory = ConverterFactory.builder().addDefaults().build();

        ParameterConverterFactory factory = ParameterConverterFactory
                .builder(errorFactory)
                .addAll(converterFactory.getConverters(String.class))
                .build();
        
        ParameterConverter<String,Integer> converter = factory.getConverter(Integer.class);
        assertEquals(Integer.valueOf(1), converter.convert("fooParam", "1", null));
    }
    
}
