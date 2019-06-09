package com.github.restup.jackson.parser;

import static com.github.restup.util.TestRegistries.mapBackedRegistry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ParsedResourceControllerRequest.Builder;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.request.parser.path.RequestPathParserResult;
import com.github.restup.errors.ErrorCode;
import com.github.restup.jackson.service.model.JacksonRequestBody;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.ResourceData;
import com.github.restup.test.utils.TestResourceUtils;
import com.model.test.company.Person;
import com.music.Label;
import java.io.IOException;
import java.net.URL;
import org.junit.Test;

public class JacksonRequestBodyParserTest {

    @Test
    public void testParseException() {

        JacksonRequestBodyParser parser = new JacksonRequestBodyParser();
        JsonNode node = mock(JsonNode.class);
        ResourceControllerRequest details = mock(ResourceControllerRequest.class);
        Builder<?> builder = mock(Builder.class);
        Resource resource = mock(Resource.class);
        when(resource.getClassType()).thenReturn(Person.class);
        when(builder.getResource()).thenReturn(resource);

        parser.deserializeObject(details, builder, new TextNode("foo"));

        verify(builder).getResource();
        verify(builder).addError(ErrorCode.BODY_INVALID);
        verifyNoMoreInteractions(details, builder);
    }

    @Test
    public void testPerson() throws IOException {
        ParsedResourceControllerRequest result = test(Person.class, "/deserialize/person.json");
        ResourceAssert.assertPaths(result.getRequestedPaths(), "/data/firstName", "/data/lastName");
    }

    @Test
    public void testPeople() throws IOException {
        ParsedResourceControllerRequest result = test(Person.class, "/deserialize/people.json");
        ResourceAssert.assertPaths(result.getRequestedPaths(), "/data/0/firstName", "/data/0/lastName", "/data/1/firstName", "/data/1/lastName");
    }

    @Test
    public void testMusicLabels() throws IOException {
        ParsedResourceControllerRequest result = test(Label.class, "/deserialize/musicLabels.json");
        ResourceAssert.assertPaths(result.getRequestedPaths()
                , "/data/0/name"
                , "/data/0/albums/0/name"
                , "/data/0/albums/0/artist/name"
                , "/data/0/albums/0/tracks/0/number"
                , "/data/0/albums/0/tracks/0/name"
                , "/data/0/albums/0/tracks/1/number"
                , "/data/0/albums/0/tracks/1/name"
                , "/data/0/albums/1/name"
                , "/data/0/albums/1/artist/name"
                , "/data/0/albums/1/tracks/0/number"
                , "/data/0/albums/1/tracks/0/name"
                , "/data/0/albums/1/tracks/1/number"
                , "/data/0/albums/1/tracks/1/name"
                , "/data/1/name"
                , "/data/1/albums/0/name"
                , "/data/1/albums/0/artist/name"
                , "/data/1/albums/0/tracks/0/number"
                , "/data/1/albums/0/tracks/0/name"
                , "/data/1/albums/0/tracks/1/number"
                , "/data/1/albums/0/tracks/1/name");
    }

    public ParsedResourceControllerRequest test(Class<?> resourceClass, String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ResourceRegistry registry = mapBackedRegistry();

        registry.registerResource(resourceClass);

        URL url = TestResourceUtils.getResource(path);

        JacksonRequestBody data = mapper.readValue(url, JacksonRequestBody.class);

        ResourceControllerRequest details = mock(ResourceControllerRequest.class);
        RequestPathParserResult requestPathParserResult = mock(RequestPathParserResult.class);
        when(details.getMethod()).thenReturn(HttpMethod.POST);
        when(details.getBody()).thenReturn((ResourceData) data);
        when(requestPathParserResult.getResource())
            .thenReturn((Resource) registry.getResource(resourceClass));

        ParsedResourceControllerRequest.Builder<?> builder = ParsedResourceControllerRequest
            .builder(registry, details, requestPathParserResult);

        JacksonRequestBodyParser parser = new JacksonRequestBodyParser(mapper);
        parser.parse(details, requestPathParserResult, builder);
        return builder.build();
    }
}
