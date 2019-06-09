package com.github.restup.controller.request.parser;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.path.RequestPathParserResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContentNegotiatedRequestParserTest {
    
    @Mock
    ResourceControllerRequest request;
    @Mock
    RequestPathParserResult requestPathParserResult;
    @Mock
    ParsedResourceControllerRequest.Builder<?> builder;
    @Mock
    RequestParser requestParser;

    @Test
    public void testNoParser() {
        ContentNegotiatedRequestParser parser = ContentNegotiatedRequestParser.builder()
                .addParser(MediaType.APPLICATION_JSON, requestParser).build();
        parser.parse(request, requestPathParserResult, builder);
        
        verify(request).getContentType();
        verifyNoMoreInteractions(request, builder, requestPathParserResult, requestParser);
    }
}
