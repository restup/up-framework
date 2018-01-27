package com.github.restup.controller.request.parser.params;

import static com.github.restup.util.TestRegistries.mapBackedRegistry;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Test;
import org.mockito.Mockito;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.query.ResourceQueryStatement.Type;
import com.github.restup.registry.Resource;
import com.model.test.company.Company;

public class FieldsParserTest {

    @Test
    public void testAccept() {
        FieldsParser parser = new FieldsParser();
        assertTrue(parser.accept("fields"));
        assertTrue(parser.accept("fields[foo]"));

        parser = new FieldsParser("bar");
        assertTrue(parser.accept("bar"));
        assertTrue(parser.accept("bar[foo]"));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testParse() {
        String param = "fields[foo]";
        ResourceControllerRequest details = Mockito.mock(ResourceControllerRequest.class);
        ParsedResourceControllerRequest.Builder b = Mockito.mock(ParsedResourceControllerRequest.Builder.class);
        FieldsParser parser = new FieldsParser();
        parser.parse(details, b, param, new String[]{"*", "+a,-b,c", null, "  ", ""});
        verify(b, times(1)).setFieldRequest(param, "*", "foo", Type.All);
        verify(b, times(1)).addAdditionalField(param, "+a,-b,c", "foo", "a");
        verify(b, times(1)).addExcludedField(param, "+a,-b,c", "foo", "b");
        verify(b, times(1)).addRequestedField(param, "+a,-b,c", "foo", "c");
        verify(b, times(1)).addParameterError(param, null);
        verify(b, times(1)).addParameterError(param, "");
        verify(b, times(1)).addParameterError(param, "  ");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testParseEvery() {
        String param = "fields";
        ResourceControllerRequest details = Mockito.mock(ResourceControllerRequest.class);
        when(details.getResource()).thenReturn((Resource) Resource.builder(Company.class).registry(mapBackedRegistry()).build());
        ParsedResourceControllerRequest.Builder b = Mockito.mock(ParsedResourceControllerRequest.Builder.class);
        FieldsParser parser = new FieldsParser();
        parser.parse(details, b, param, new String[]{"**"});
        verify(b, times(1)).setFieldRequest(param, "**", "company", Type.Every);
    }

}
