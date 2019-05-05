package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.fields;
import static com.github.restup.controller.request.parser.params.ParameterParser.ParameterParsers.Bracketed;
import static com.github.restup.controller.request.parser.params.ParameterValueParser.ParameterValueParsers.FIELDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.restup.controller.request.parser.RequestParamParser;
import com.github.restup.query.ResourceQueryStatement.Type;
import org.junit.Before;
import org.junit.Test;

public class FieldsRequestParamParserTest extends AbstractRequestParamParserTest {

    protected RequestParamParser parser = fields().build();
    protected RequestParamParser parserBrackets = fields(Bracketed).build();

    @Override
    protected String getParameterName() {
        return ComposedRequestParamParser.FIELDS;
    }


    @Override
    @Before
    public void before() {
        when(ctx.getBuilder()).thenReturn(builder);
        when(result.getResource()).thenReturn(resultResource);
        when(request.getResource()).thenReturn(requestedResource);
    }


    @Test
    public void testAccept() {
        assertTrue(parser.accept("fields"));
        assertFalse(parser.accept("felds"));
        assertFalse(parser.accept("fields[foo]"));
    }

    @Test
    public void testAcceptBrackets() {
        assertFalse(parserBrackets.accept("fields"));
        assertFalse(parserBrackets.accept("felds"));
        assertTrue(parserBrackets.accept("fields[foo]"));
    }

    @Override
    protected void verifyParameterValueParser() {
        verify(ctx).getBuilder();
        verify(result).getResource();
        verifyDefault();
    }

    @Test
    public void testParameterValueParserAll() {
        FIELDS.parse(ctx, result, "  * ", "*");
        verify(builder).setFieldRequest(resultResource, Type.All);
        verifyParameterValueParser();
    }

    @Test
    public void testParameterValueParserEvery() {
        FIELDS.parse(ctx, result, "  ** ", "**");
        verify(builder).setFieldRequest(resultResource, Type.Every);
        verifyParameterValueParser();
    }

    @Test
    public void testParameterValueParserAdd() {
        FIELDS.parse(ctx, result, "  +foo ", "+foo");
        verify(builder).addAdditionalField(resultResource, "foo");
        verifyParameterValueParser();
    }

    @Test
    public void testParameterValueParserExclude() {
        FIELDS.parse(ctx, result, "  -foo ", "-foo");
        verify(builder).addExcludedField(resultResource, "foo");
        verifyParameterValueParser();
    }

    @Test
    public void testParameterValueParserField() {
        FIELDS.parse(ctx, result, "  foo ", "foo");
        verify(builder).addRequestedField(resultResource, "foo");
        verifyParameterValueParser();
    }

    @Test
    public void testRequestParamParser() {
        parser.parse(request, builder, getParameterName(), " *, +foo, -bar ", " baz ");
        // new ctx is created
        verify(builder).setFieldRequest(requestedResource, Type.All);
        verify(builder).addAdditionalField(requestedResource, "foo");
        verify(builder).addExcludedField(requestedResource, "bar");
        verify(builder).addRequestedField(requestedResource, "baz");
        verifyRequestParamParser();
    }

}
