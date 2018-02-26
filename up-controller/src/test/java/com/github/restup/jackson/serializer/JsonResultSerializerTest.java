package com.github.restup.jackson.serializer;

import static com.github.restup.test.ContentsAssertions.json;
import static com.github.restup.util.ReflectionUtils.getAnnotation;
import static com.github.restup.util.TestRegistries.mapBackedRegistryBuilder;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import javax.persistence.Transient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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

@RunWith(MockitoJUnitRunner.class)
public class JsonResultSerializerTest {

    ObjectMapper mapper = JacksonConfiguration.configure();
    @Mock
    ParsedResourceControllerRequest<?> request;
    Resource<A2J, ?> a2j;
    Resource<Shallow, ?> shallow;

    @Before
    public void setup() {
        ResourceRegistry registry = mapBackedRegistryBuilder()
                .mappedFieldBuilderVisitors(
                        new IdentityByConventionMappedFieldBuilderVisitor()
                        , new MappedFieldBuilderVisitor() {
                            @Override
                            public <T> void visit(MappedField.Builder<T> b, ReflectionUtils.BeanInfo<T> bi, ReflectionUtils.PropertyDescriptor pd) {
                                b.transientField(null != getAnnotation(Transient.class, pd));
                            }
                        }).build();

        registry.registerResources(A2J.class, Shallow.class);

        a2j = registry.getResource(A2J.class);
        shallow = registry.getResource(Shallow.class);
    }

    private JsonResult result(Object object) {
        return new JsonResult(request, ReadResult.of(object));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Builder a2j() {
        when(request.getResource()).thenReturn((Resource) a2j);
        return json(mapper);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Builder shallow() {
        when(request.getResource()).thenReturn((Resource) shallow);
        return json(mapper);
    }

    private void query(Resource<?, ?> resource, String... paths) {
        when(request.getRequestedQueries()).thenReturn(Arrays.asList(
                ResourceQueryStatement.builder(resource)
                        .addRequestedPaths(paths)
                        .build()));
    }

    @Test
    public void testNull() throws JsonProcessingException {
        a2j().expect(result(null))
                .matches("{\"data\":null}");
    }

    @Test
    public void testA2J() throws JsonProcessingException {
        a2j().test("a2j.json")
                .matches(result(new A2J(1l, "A", "B", "C", "D", "E", "F", "G", "H", "I", "J")));;
    }

    @Test
    public void testGraph() throws JsonProcessingException {
        shallow().test("graph.json").matches(result(Shallow.graph()));
    }

    @Test
    public void testPath() throws JsonProcessingException {
        query(shallow, "name", "deep.name", "deep.deeper.name", "deep.deeper.deepest.name");
        shallow().test("path.json").matches(result(Shallow.graph()));
    }

    @Test
    public void testPathArraysIndexed() throws JsonProcessingException {
        query(shallow, "name", "deep.name", "deep.deeper.name", "deep.deeper.deepest.name"
                , "deeps.1.name", "deeps.0.deepers.1.name", "deeps.1.deepers.0.deepests.0.name");
        shallow().test("pathArraysIndexed.json").matches(result(Shallow.graph()));
    }

    @Test
    public void testPathArrays() throws JsonProcessingException {
        query(shallow, "name", "deep.name", "deep.deeper.name", "deep.deeper.deepest.name"
                , "deeps.name", "deeps.deepers.name", "deeps.deepers.deepests.name");
        shallow().test("pathArrays.json").matches(result(Shallow.graph()));
    }

}
