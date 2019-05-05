package com.github.restup.controller.request.parser.params;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.errors.ParameterError;
import com.github.restup.errors.RequestError;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
abstract class AbstractRequestParamParserTest {

    @Mock
    protected ParameterParsingContext ctx;
    @Mock
    protected ParameterParserResult result;
    @Mock
    protected ParsedResourceControllerRequest.Builder builder;
    @Mock
    protected ResourceControllerRequest request;
    @Mock
    protected Resource requestedResource;
    @Mock
    protected Resource resultResource;
    @Mock
    protected ResourceRegistry registry;

    static void assertParameterErrorMeta(RequestError error, String resourceName,
        String parameterName,
        Object errObject) {

        Map<String, String> meta = (Map) error.getMeta();
        assertEquals(3, meta.size());
        assertEquals(resourceName, meta.get("resource"));
        assertEquals(parameterName, meta.get("parameterName"));
        assertEquals(errObject, meta.get("parameterValue"));
        assertEquals(error.getId(), UUID.fromString(error.getId()).toString());
        assertEquals(400, error.getHttpStatus());
        assertEquals("400", error.getStatus());
    }

    static void assertParameterError(RequestError error, String parameterName,
        Object errObject) {
        ParameterError parameterError = (ParameterError) error.getSource();
        assertEquals(parameterName, parameterError.getParameterName());
        assertEquals(errObject, parameterError.getParameterValue());
    }

    abstract String getParameterName();

    @Before
    public void before() {
        when(ctx.getBuilder()).thenReturn(builder);
        when(ctx.getRawParameterName()).thenReturn(getParameterName());
        doCallRealMethod().when(builder).addParameterError(anyString(), any());
        doCallRealMethod().when(builder).addParameterError(any(), anyString(), any());
        when(builder.getParameterError(any(), any())).thenCallRealMethod();
//        doCallRealMethod().when(builder).addError(any(RequestError.Builder.class));
        when(builder.getErrorFactory()).thenReturn(ErrorFactory.getDefaultErrorFactory());
        when(request.getResource()).thenReturn(requestedResource);
        when(requestedResource.getName()).thenReturn("testResource");
//        when(resource.getRegistry()).thenReturn(registry);
    }

    protected void verifyDefault() {
        verifyNoMoreInteractions(ctx, result, builder, request, requestedResource, resultResource,
            registry);
    }

    protected void verifyParameterValueParser() {
        verify(ctx).getBuilder();
        verify(ctx).getRawParameterName();
        verifyDefault();
    }

    protected void verifyRequestParamParser() {
        verify(request).getResource();
        verifyDefault();
    }

    protected void verifyRequestParamParserError(Object errObject) {
        verifyRequestParamParserError(getParameterName(), errObject);
    }

    protected void verifyRequestParamParserError(String parameterName, Object errObject) {
        verifyError(parameterName, errObject, requestErrorConsumer(parameterName, errObject));
        verifyRequestParamParser();
    }

    protected void verifyParameterValueParserError(Object errObject) {
        verifyParameterValueParserError(getParameterName(), errObject);
    }

    protected void verifyParameterValueParserError(String parameterName, Object errObject) {
        verifyError(parameterName, errObject, requestErrorConsumer(parameterName, errObject));
        verifyParameterValueParser();
    }

    private Consumer<RequestError> requestErrorConsumer(String parameterName, Object errObject) {
        return (error) -> {
            assertEquals("PARAMETER_INVALID", error.getCode());
            assertEquals("Invalid parameter value", error.getTitle());
            assertEquals("'" + errObject + "' is not a valid value for " + parameterName,
                error.getDetail());

            assertParameterError(error, parameterName, errObject);
            assertParameterErrorMeta(error, parameterName, errObject);
        };
    }

    protected void assertParameterErrorMeta(RequestError error, String parameterName,
        Object errObject) {
        assertParameterErrorMeta(error, requestedResource.getName(), parameterName, errObject);
    }

    protected void verifyRequestParamParserError(String parameterName, Object errObject,
        Consumer<RequestError> requestErrorConsumer) {
        verifyError(parameterName, errObject, requestErrorConsumer);
        verifyRequestParamParser();
    }

    protected void verifyParameterValueParserError(String parameterName, Object errObject,
        Consumer<RequestError> requestErrorConsumer) {
        verifyError(parameterName, errObject, requestErrorConsumer);
        verifyParameterValueParser();
    }

    protected void verifyError(String parameterName, Object errObject,
        Consumer<RequestError> requestErrorConsumer) {
        verify(builder).addParameterError(parameterName, errObject);
        verify(builder).getParameterError(parameterName, errObject);
        verify(builder).getErrorFactory();
        ArgumentCaptor<RequestError.Builder> captor = ArgumentCaptor
            .forClass(RequestError.Builder.class);
        verify(builder).addError(captor.capture());
        RequestError error = captor.getValue()
            .resource(requestedResource) // simulate addError
            .build();

        requestErrorConsumer.accept(error);
        verify(requestedResource, times(2)).getName();
    }

}
