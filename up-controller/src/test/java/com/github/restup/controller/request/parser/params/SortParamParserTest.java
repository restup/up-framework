package com.github.restup.controller.request.parser.params;

import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.SORT;
import static com.github.restup.controller.request.parser.params.ComposedRequestParamParser.sort;
import static com.github.restup.controller.request.parser.params.ParameterParser.ParameterParsers.Bracketed;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.restup.controller.request.parser.RequestParamParser;
import org.junit.Before;
import org.junit.Test;

public class SortParamParserTest extends AbstractRequestParamParserTest {

    protected RequestParamParser parser = sort().build();
    protected RequestParamParser parserBrackets = sort(Bracketed).build();

    @Override
    protected String getParameterName() {
        return SORT;
    }

    @Override
    @Before
    public void before() {
//        when(ctx.getBuilder()).thenReturn(builder);
//        when(result.getResource()).thenReturn(resultResource);
        when(request.getResource()).thenReturn(requestedResource);
    }


    @Test
    public void testAccept() {
        assertTrue(parser.accept("sort"));
        assertFalse(parser.accept("sorted"));
        assertFalse(parser.accept("sort[foo]"));
    }

    @Test
    public void testAcceptBrackets() {
        assertFalse(parserBrackets.accept("sort"));
        assertFalse(parserBrackets.accept("sort"));
        assertTrue(parserBrackets.accept("sort[foo]"));
    }

    @Test
    public void testRequestParamParserDelimited() {
        parser.parse(request, builder, getParameterName(), "a ,, -b ,+c");
        verify(builder).addSort(SORT, "a ", "a", null);
        verify(builder).addSort(SORT, " -b ", "b", false);
        verify(builder).addSort(SORT, "+c", "c", true);
        verifyRequestParamParser();
    }

    @Test
    public void testRequestParamParserAscending() {
        parser.parse(request, builder, getParameterName(), "+a");
        verify(builder).addSort(SORT, "+a", "a", true);
        verifyRequestParamParser();
    }

    @Test
    public void testRequestParamParserDescending() {
        parser.parse(request, builder, getParameterName(), "-a");
        verify(builder).addSort(SORT, "-a", "a", false);
        verifyRequestParamParser();
    }

    @Test
    public void testRequestParamParserDefault() {
        parser.parse(request, builder, getParameterName(), " a ");
        verify(builder).addSort(SORT, " a ", "a", null);
        verifyRequestParamParser();
    }

}
