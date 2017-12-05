package com.github.restup.jackson.parser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.jackson.service.model.JacksonRequestBody;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.settings.RegistrySettings;
import com.github.restup.repository.collections.MapBackedRepositoryFactory;
import com.github.restup.service.model.ResourceData;
import com.github.restup.test.utils.TestResourceUtils;
import com.model.test.company.Person;
import com.music.Label;
import java.io.IOException;
import java.net.URL;
import org.junit.Test;

@SuppressWarnings({"rawtypes", "unchecked"})
public class JacksonRequestBodyParserTest {

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
        ResourceRegistry registry = new ResourceRegistry(RegistrySettings.builder()
                .repositoryFactory(new MapBackedRepositoryFactory()));

        registry.registerResource(resourceClass);

        URL url = TestResourceUtils.getResource(path);

        JacksonRequestBody data = mapper.readValue(url, JacksonRequestBody.class);

        ResourceControllerRequest details = mock(ResourceControllerRequest.class);
        when(details.getMethod()).thenReturn(HttpMethod.POST);
        when(details.getBody()).thenReturn((ResourceData) data);
        when(details.getResource()).thenReturn((Resource) registry.getResource(resourceClass));

        ParsedResourceControllerRequest.Builder<?> builder = ParsedResourceControllerRequest.builder(registry, details);

        JacksonRequestBodyParser parser = new JacksonRequestBodyParser(mapper);
        parser.parse(details, builder);
        return builder.build();
    }
}
