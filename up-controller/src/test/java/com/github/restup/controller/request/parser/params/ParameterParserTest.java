package com.github.restup.controller.request.parser.params;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.github.restup.controller.request.parser.params.ParameterParser.ParameterParsers;
import com.github.restup.registry.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ParameterParserTest {

    @Mock
    protected ParameterParsingContext ctx;
    @Mock
    private Resource resource;

    @Test
    public void testBrackets() {
        ParameterParser parser = ParameterParsers.Bracketed;
        when(ctx.getResource("foo")).thenReturn(resource);
        assertValues(parser.parse(ctx, "fields[foo]"), "foo");
        assertValues(parser.parse(ctx, "filter[foo][gt]"), "foo", "gt");
        assertValues(parser.parse(ctx, "f[foo][gt]"), "foo", "gt");

        verify(ctx, times(3)).getResource("foo");
        verifyNoMoreInteractions(ctx, resource);
    }
//
//    @Test
//    public void testSnakecase() {
//        ParameterParser<String[]> parser = ParameterParsers.SnakeCase;
//        assertValues(parser.parse(null, "foo"), "foo");
//        assertValues(parser.parse(null, "foo_gt"), "foo", "gt");
//        assertValues(parser.parse(null, "resource_name_gt"), "resource_name", "gt");
//    }

//
//     * //     * sort_resource_name //     * offset_resource_name //     * limit_resource_name //
//         **page_number_resource_name //     * resource_name_gt

    private void assertValues(ParameterParserResult result, String... expected) {
        String[] tokens = result.getTokens();
        assertEquals(expected.length, tokens.length);
        for (int i = 0; i < tokens.length; i++) {
            assertEquals(expected[i], tokens[i]);
        }
    }

}
