package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.AbstractRequestParamParserTest.assertParameterError;
import static com.github.restup.controller.request.parser.params.AbstractRequestParamParserTest.assertParameterErrorMeta;
import static com.github.restup.controller.request.parser.params.ParameterResourceParser.ParameterResourceParsers.Parsed;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.errors.RequestError;
import com.github.restup.registry.Resource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ParameterResourceParserTest {

    private final String[] values = new String[]{"10"};
    @Mock
    protected ParameterParsingContext ctx;
    @Mock
    private Resource resource;
    @Mock
    private ParsedResourceControllerRequest.Builder requestBuilder;
    @Mock
    private ResourceControllerRequest request;

    @Before
    public void setup() {
        when(ctx.getResource("foo")).thenReturn(resource);
        when(ctx.getBuilder()).thenReturn(requestBuilder);
        when(ctx.getRequest()).thenReturn(request);
        when(ctx.getRawParameterName()).thenReturn("filter[foo][gt]");
        when(ctx.getRawParameterValues()).thenReturn(values);
        when(request.getResource()).thenReturn(resource);
        when(resource.getName()).thenReturn("foo");
        when(requestBuilder.getErrorFactory()).thenReturn(ErrorFactory.getDefaultErrorFactory());
    }

    @After
    public void after() {
        verifyNoMoreInteractions(ctx, resource, requestBuilder, request);
    }

    @Test
    public void testParseResource() {
        assertNotNull(Parsed.parseResource(ctx, "bogus", "foo", "gt"));
        verify(ctx).getResource("foo");
        verify(ctx).getResource("gt");
    }

    @Test
    public void testParseResourceAt() {
        assertNotNull(Parsed.parseResourceAt(ctx, 0, "foo", "gt"));
        verify(ctx).getResource("foo");
    }


    private void setupError() {
        when(ctx.getRawParameterName()).thenReturn("filter[bar][gt]");
    }

    @Test
    public void testParseResourceError() {
        setupError();
        assertNull(Parsed.parseResource(ctx, "bar", "gt"));
        verifyError("bar", "gt");
    }

    @Test
    public void testParseResourceAtError() {
        setupError();
        assertNull(Parsed.parseResourceAt(ctx, 0, "bar", "gt"));
        verifyError("bar");
    }

    private void verifyError(String... resourceLookupAttempts) {
        ArgumentCaptor<RequestError.Builder> errorCaptor = ArgumentCaptor
            .forClass(RequestError.Builder.class);
        verify(ctx).addError(errorCaptor.capture());
        RequestError error = errorCaptor.getValue().build();

        assertEquals("PARAMETER_INVALID_RESOURCE", error.getCode());
        assertEquals("Invalid parameter resource specified", error.getTitle());
        assertEquals("Parameter 'filter[bar][gt]' does not specify a valid resource",
            error.getDetail());
        assertParameterErrorMeta(error, "foo", "filter[bar][gt]", values);
        assertParameterError(error, "filter[bar][gt]", values);

        for (String attempt : resourceLookupAttempts) {
            verify(ctx).getResource(attempt);
        }
        verify(ctx).getBuilder();
        verify(ctx).getRequest();
        verify(ctx, times(2)).getRawParameterName();
        verify(ctx).getRawParameterValues();
        verify(ctx).addError(any());
        verify(request).getResource();
        verify(resource).getName();
        verify(requestBuilder).getErrorFactory();
    }
}
