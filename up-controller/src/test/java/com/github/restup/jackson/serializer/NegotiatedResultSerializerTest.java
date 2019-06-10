package com.github.restup.jackson.serializer;

import static com.github.restup.assertions.Assertions.assertThrows;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.result.JsonResult;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.path.EmbeddedResourcePathValue;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.response.ResourceResult;
import com.model.test.company.Person;
import java.io.IOException;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NegotiatedResultSerializerTest {


    @Mock
    JsonGenerator jsonGenerator;
    @Mock
    SerializerProvider provider;
    @Mock
    JsonResult result;
    @Mock
    ResourceResult<?> resourceResult;
    @Mock
    ParsedResourceControllerRequest<?> resourceRequest;
    @Mock
    Resource<?,?> resource;

    @Test
    public void testSerializeException() throws IOException {
        doThrow(NullPointerException.class).when(jsonGenerator).writeStartObject();
        when(result.getResult()).thenReturn(resourceResult);
        when(result.getRequest()).thenReturn((ParsedResourceControllerRequest)resourceRequest);
        when(resourceRequest.getResource()).thenReturn((Resource)resource);
        
        JsonResultSerializer serializer = new JsonResultSerializer();
        assertThrows(()-> serializer.serialize(result, jsonGenerator, provider));
    }
    
    @Test
    public void testGetResource() {
        EmbeddedResourcePathValue pathValue = mock(EmbeddedResourcePathValue.class);
        when(pathValue.getResource()).thenReturn((Resource)resource);
        JsonResultSerializer serializer = new JsonResultSerializer();
        assertEquals(resource, serializer.getResource(pathValue));
    }
    
    @Test
    public void testWriteArray() throws Exception {
        when(resource.getIdentityField()).thenReturn(new MappedField[]{mock(MappedField.class)});
        JsonResultSerializer serializer = new JsonResultSerializer();
        
        serializer.writeObject(resource, Collections.emptyMap(), new Object[] {new Person()}, result, jsonGenerator, provider);

        verify(jsonGenerator).writeStartArray();
        verify(jsonGenerator).writeEndArray();
    }

}
