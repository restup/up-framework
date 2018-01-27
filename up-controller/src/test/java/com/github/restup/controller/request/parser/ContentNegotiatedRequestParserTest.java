package com.github.restup.controller.request.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;

@RunWith(MockitoJUnitRunner.class)
public class ContentNegotiatedRequestParserTest {
    
    @Mock
    ResourceControllerRequest request;
    @Mock
    ParsedResourceControllerRequest.Builder<?> builder;
    @Mock
    RequestParser requestParser;

    @Test
    public void testNoParser() {
        ContentNegotiatedRequestParser parser = ContentNegotiatedRequestParser.builder()
                .addParser(MediaType.APPLICATION_JSON, requestParser).build();
        parser.parse(request, builder);
        
        verify(request).getContentType();
        verifyNoMoreInteractions(request, builder, requestParser);
    }
}
