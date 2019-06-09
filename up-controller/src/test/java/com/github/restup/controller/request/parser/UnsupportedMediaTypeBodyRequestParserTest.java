package com.github.restup.controller.request.parser;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.github.restup.assertions.Assertions;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.path.RequestPathParserResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UnsupportedMediaTypeBodyRequestParserTest {

    @Mock
    ResourceControllerRequest request;
    @Mock
    ParsedResourceControllerRequest.Builder<?> builder;
    @Mock
    RequestPathParserResult requestPathParserResult;

    @Test
    public void testParseException() {
        UnsupportedMediaTypeBodyRequestParser parser = new UnsupportedMediaTypeBodyRequestParser();

        Assertions.assertThrows(() -> parser.parse(request, requestPathParserResult, builder))
            .code("UNSUPPORTED_MEDIA_TYPE");

//        verify(request).getResource();
        verify(request).getContentType();
        verify(requestPathParserResult).getResource();
        verifyNoMoreInteractions(request, requestPathParserResult, builder);
    }
}
