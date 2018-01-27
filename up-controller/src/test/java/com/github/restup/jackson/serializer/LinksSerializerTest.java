package com.github.restup.jackson.serializer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.fasterxml.jackson.core.JsonGenerator;
import com.github.restup.controller.linking.Link;

@RunWith(MockitoJUnitRunner.class)
public class LinksSerializerTest {
    
    @Mock
    Link link;
    @Mock
    JsonGenerator jsonGenerator;
    
    @Test
    public void testWriteLinksObject() throws IOException {
        when(link.getName()).thenReturn("foo");
        LinksSerializer.writeLinksObject(jsonGenerator, Arrays.asList(link));
        verify(jsonGenerator).writeStartObject();
        verify(jsonGenerator).writeObjectField("foo", link);
        verify(jsonGenerator).writeEndObject();
        verify(link).getName();
        verifyNoMoreInteractions(link);
    }

}
