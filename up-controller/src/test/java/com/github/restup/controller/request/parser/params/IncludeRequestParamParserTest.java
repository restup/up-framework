package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.include;
import static com.github.restup.controller.request.parser.params.ParameterParser.ParameterParsers.Bracketed;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.github.restup.controller.request.parser.RequestParamParser;
import com.github.restup.query.ResourceQueryStatement.Type;
import com.github.restup.registry.Resource;
import org.junit.Before;
import org.junit.Test;

public class IncludeRequestParamParserTest extends AbstractRequestParamParserTest {

    protected RequestParamParser parser = include().build();
    protected RequestParamParser parserBrackets = include(Bracketed).build();

    private IncludeParameterValueParser include = IncludeParameterValueParser.forParser(Bracketed);

    @Override
    protected String getParameterName() {
        return ComposedRequestParamParser.INCLUDE;
    }


    @Override
    @Before
    public void before() {
        when(ctx.getBuilder()).thenReturn(builder);
//        when(ctx.getRequest()).thenReturn(request);
        when(builder.getResource()).thenReturn(requestedResource);
        when(requestedResource.getRegistry()).thenReturn(registry);
//        when(builder.getErrorFactory()).thenReturn(ErrorFactory.getDefaultErrorFactory());
    }

    @Test
    public void testAccept() {
        assertTrue(parser.accept("include"));
        assertFalse(parser.accept("query"));
        assertFalse(parser.accept("include[foo]"));
    }

    @Test
    public void testAcceptBrackets() {
        assertFalse(parserBrackets.accept("include"));
        assertFalse(parserBrackets.accept("query"));
        assertTrue(parserBrackets.accept("include[foo]"));
    }

    @Override
    protected void verifyParameterValueParser() {
        verify(ctx).getBuilder();
        verify(result).getResource();
        verifyDefault();
    }

    @Test
    public void testParameterValueParser() {
        when(ctx.getResource("foo")).thenReturn(resultResource);
        include.parse(ctx, result, " foo ", "foo[goodBarId]");
        verify(ctx).getResource("foo");
        verify(ctx, times(2)).getBuilder();
        verify(builder).setFieldRequest(resultResource, Type.Default);
        verify(builder).addIncludeJoinPaths(resultResource, "goodBarId");
        verifyDefault();
    }

    @Test
    public void testRequestParamParser() {
        Resource foo = mock(Resource.class);
        Resource bar = mock(Resource.class);
        when(registry.getResource("foo")).thenReturn(foo);
        when(registry.getResource("bar")).thenReturn(bar);
        parser.parse(request, builder, getParameterName(), " foo , bar ");
        // new ctx is created
        verify(builder, times(1)).getResource();
        verify(requestedResource, times(2)).getRegistry();
        verify(registry).getResource("foo");
        verify(registry).getResource("bar");
        verify(builder).setFieldRequest(foo, Type.Default);
        verify(builder).setFieldRequest(bar, Type.Default);
        verifyDefault();
    }

    @Test
    public void testRequestParamParserBracketed() {
        Resource foo = mock(Resource.class);
        Resource bar = mock(Resource.class);
        Resource baz = mock(Resource.class);
        when(registry.getResource("foo")).thenReturn(foo);
        when(registry.getResource("bar")).thenReturn(bar);
        when(registry.getResource("baz")).thenReturn(baz);
        parserBrackets
            .parse(request, builder, "include[foo]", " baz, bar[goodBarId] ");
        // new ctx is created
        verify(builder, times(1)).getResource();
        verify(requestedResource, times(3)).getRegistry();
        verify(registry).getResource("foo");
        verify(registry).getResource("bar");
        verify(registry).getResource("baz");
        verify(builder).setFieldRequest(bar, Type.Default);
        verify(builder).setFieldRequest(baz, Type.Default);
        verify(builder).addIncludeJoinPaths(bar, "goodBarId");
//        verify(ctx, times(3)).getBuilder();
        verifyDefault();
    }

}
