package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.filter;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.restup.controller.request.parser.RequestParamParser;
import com.github.restup.registry.Resource;
import org.junit.Before;
import org.junit.Test;

public class FilterRequestParamParserTest extends AbstractRequestParamParserTest {

    protected RequestParamParser parser = filter().build();

    @Override
    protected String getParameterName() {
        return ComposedRequestParamParser.FILTER;
    }

    @Override
    @Before
    public void before() {
        when(request.getResource()).thenReturn(requestedResource);
        when(requestedResource.getRegistry()).thenReturn(registry);
    }

    @Test
    public void testAccept() {
        assertFalse(parser.accept("filter"));
        assertTrue(parser.accept("filter[foo]"));
        assertTrue(parser.accept("filter[foo][gt]"));
        assertTrue(parser.accept("filter[foo][bar][gt]"));
        assertFalse(parser.accept("tot"));
    }

    @Test
    public void testParseField() {
        String param = "filter[foo]";
        parser.parse(request, builder, param, "123");
        verify(builder).addFilter(requestedResource, param, "123", "foo", (String) null, "123");
    }

    @Test
    public void testParseFieldAndFunction() {
        String param = "filter[foo][gt]";
        parser.parse(request, builder, param, "22");
        verify(builder).addFilter(requestedResource, param, "22", "foo", "gt", "22");
    }

    @Test
    public void testParseResourceFieldAndFunction() {
        Resource foo = mock(Resource.class);
        when(registry.getResource("foo")).thenReturn(foo);
        when(foo.getName()).thenReturn("foo");
        String param = "filter[foo][bar][lt]";
        parser.parse(request, builder, param, "33");
        verify(builder).addFilter(foo, param, "33", "bar", "lt", "33");
    }

    @Test
    public void testParseBad() {
        bad("filter[foo");
        bad("filter[foo][");
        bad("filter[]");
        bad("filter[foo][]");
        bad("filter[foo][bar");
        bad("filter[foo][bar][");
        bad("filter[foo][bar][]");
        bad("filter[foo][bar][boo]");
    }

    private void bad(String param) {
//        Builder b = Mockito.mock(Builder.class);
//        FilterParameterValueParser parser = new FilterParameterValueParser(param);
//        parser.parse(null, b, param, new String[]{"a"});
//        verify(b, times(0)).addFilter(any(String.class), any(String.class), any(String.class), nullable(String.class), any(String.class));
//        verify(b, times(1)).addParameterError(any(String.class), any(Object.class));
    }

}
