package com.github.restup.controller.request.parser;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.github.restup.assertions.Assertions;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;

@RunWith(MockitoJUnitRunner.class)
public class UnsupportedMediaTypeBodyRequestParserTest {

    @Mock
    ResourceControllerRequest request;
    @Mock
    ParsedResourceControllerRequest.Builder<?> builder;

    @Test
    public void testParseException() {
        UnsupportedMediaTypeBodyRequestParser parser = new UnsupportedMediaTypeBodyRequestParser();

        Assertions.assertThrows(() -> parser.parse(request, null))
                .code("UNSUPPORTED_MEDIA_TYPE")
        ;
        verify(request).getResource();
        verify(request).getContentType();
        verifyNoMoreInteractions(request, builder);
    }
}
