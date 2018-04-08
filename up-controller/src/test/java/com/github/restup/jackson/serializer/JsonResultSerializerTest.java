package com.github.restup.jackson.serializer;

import static com.github.restup.test.ContentsAssertions.assertJson;
import static com.github.restup.util.ReflectionUtils.getAnnotation;
import static com.github.restup.util.TestRegistries.mapBackedRegistryBuilder;
import static org.mockito.Mockito.when;

import com.deep.Shallow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.result.JsonResult;
import com.github.restup.jackson.JacksonConfiguration;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderVisitor;
import com.github.restup.mapping.fields.visitors.IdentityByConventionMappedFieldBuilderVisitor;
import com.github.restup.query.ResourceQueryStatement;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.response.ReadResult;
import com.github.restup.test.ContentsAssertions.Builder;
import com.github.restup.util.ReflectionUtils;
import com.many.fields.A2J;
import java.util.Arrays;
import javax.persistence.Transient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JsonResultSerializerTest {

    ObjectMapper mapper = JacksonConfiguration.configure();
    @Mock
    ParsedResourceControllerRequest<?> request;
    Resource<A2J, ?> a2j;
    Resource<Shallow, ?> shallow;

    @Before
    public void setup() {
        final ResourceRegistry registry = mapBackedRegistryBuilder()
                .mappedFieldBuilderVisitors(
                        new IdentityByConventionMappedFieldBuilderVisitor()
                        , new MappedFieldBuilderVisitor() {
                            @Override
                            public <T> void visit(
                                final MappedField.Builder<T> b,
                                final ReflectionUtils.BeanInfo<T> bi,
                                final ReflectionUtils.PropertyDescriptor pd) {
                                b.transientField(null != getAnnotation(Transient.class, pd));
                            }
                        }).build();

        registry.registerResources(A2J.class, Shallow.class);

        this.a2j = registry.getResource(A2J.class);
        this.shallow = registry.getResource(Shallow.class);
    }

    private JsonResult result(final Object object) {
        return new JsonResult(this.request, ReadResult.of(object));
    }

    private Builder a2j() {
        when(this.request.getResource()).thenReturn((Resource) this.a2j);
        return assertJson(this.mapper);
    }

    private Builder shallow() {
        when(this.request.getResource()).thenReturn((Resource) this.shallow);
        return assertJson(this.mapper);
    }

    private void query(final Resource<?, ?> resource, final String... paths) {
        when(this.request.getRequestedQueries()).thenReturn(Arrays.asList(
                ResourceQueryStatement.builder(resource)
                        .addRequestedPaths(paths)
                        .build()));
    }

    @Test
    public void testNull() throws JsonProcessingException {
        this.a2j().expect(this.result(null))
                .matches("{\"data\":null}");
    }

    @Test
    public void testA2J() throws JsonProcessingException {
        this.a2j().test("a2j.json")
            .matches(this.result(new A2J(1l, "A", "B", "C", "D", "E", "F", "G", "H", "I", "J")));
    }

    @Test
    public void testGraph() throws JsonProcessingException {
        this.shallow().test("graph.json").matches(this.result(Shallow.graph()));
    }

    @Test
    public void testPath() throws JsonProcessingException {
        this.query(this.shallow, "name", "deep.name", "deep.deeper.name",
            "deep.deeper.deepest.name");
        this.shallow().test("path.json").matches(this.result(Shallow.graph()));
    }

    @Test
    public void testPathArraysIndexed() throws JsonProcessingException {
        this.query(this.shallow, "name", "deep.name", "deep.deeper.name", "deep.deeper.deepest.name"
                , "deeps.1.name", "deeps.0.deepers.1.name", "deeps.1.deepers.0.deepests.0.name");
        this.shallow().test("pathArraysIndexed.json").matches(this.result(Shallow.graph()));
    }

    @Test
    public void testPathArrays() throws JsonProcessingException {
        this.query(this.shallow, "name", "deep.name", "deep.deeper.name", "deep.deeper.deepest.name"
                , "deeps.name", "deeps.deepers.name", "deeps.deepers.deepests.name");
        this.shallow().test("pathArrays.json").matches(this.result(Shallow.graph()));
    }

}
