package com.github.restup.query;

import static com.github.restup.path.ResourcePath.path;
import static com.github.restup.query.PreparedResourceQueryStatement.criteria;
import static com.github.restup.query.PreparedResourceQueryStatement.queryFields;
import static com.github.restup.query.PreparedResourceQueryStatement.sparseFields;
import static com.github.restup.util.ReflectionUtils.getAnnotation;
import static com.github.restup.util.ResourceAssert.assertBeanPaths;
import static com.github.restup.util.TestRegistries.mapBackedRegistryBuilder;
import static org.junit.Assert.assertEquals;

import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderVisitor;
import com.github.restup.path.ResourcePath;
import com.github.restup.path.ResourcePathsProvider;
import com.github.restup.query.ResourceQueryStatement.Builder;
import com.github.restup.query.ResourceQueryStatement.Type;
import com.github.restup.query.criteria.ResourcePathFilter;
import com.github.restup.query.criteria.ResourcePathFilter.Operator;
import com.github.restup.query.criteria.ResourceQueryCriteria;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.util.ReflectionUtils;
import com.many.fields.A2J;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Transient;
import org.junit.Before;
import org.junit.Test;

public class PreparedResourceQueryStatementTest {

    ResourceRegistry registry;
    Resource<A2J, ?> a2j;

    TestResourcePathsProvider sparseFieldsDefaultsProvider;
    TestResourcePathsProvider restrictedFieldsProvider;

    @Before
    public void setup() {
        sparseFieldsDefaultsProvider = new TestResourcePathsProvider();
        sparseFieldsDefaultsProvider.delegate = ResourcePathsProvider.allApiFields();
        restrictedFieldsProvider = new TestResourcePathsProvider();
        ResourceRegistry registry = mapBackedRegistryBuilder()
            .mappedFieldVisitorBuilder(

                MappedFieldBuilderVisitor.builder()
                    .withIdentityConvention("id")
                    .add(
                        new MappedFieldBuilderVisitor() {
                            @Override
                            public <T> void visit(MappedField.Builder<T> b, ReflectionUtils.BeanInfo<T> bi, ReflectionUtils.PropertyDescriptor pd) {
                                b.transientField(null != getAnnotation(Transient.class, pd));
                            }
                        })).build();
        registry
                .registerResource(Resource.builder(A2J.class)
                        .sparseFieldsProvider(sparseFieldsDefaultsProvider)
                        .restrictedFieldsProvider(restrictedFieldsProvider));

        a2j = registry.getResource(A2J.class);
    }

    private Builder a2j(Type type, String... paths) {
        return new Builder(a2j).setType(type).addRequestedPaths(paths);
    }

    private Builder a2j() {
        return a2j(Type.Default);
    }

    @Test
    public void testCriteria() {
        Builder query = a2j().addCriteria("a", 1)
                .addCriteria("b", 2);
        List<ResourceQueryCriteria> result = criteria(query.build(), null);
        assertCriteria(result,
                Arrays.asList("a", "b"),
                1, 2);

        ResourceQueryDefaults defaults = new ResourceQueryDefaults(a2j, query.build());
        defaults.addCriteria("c", 3);
        result = criteria(query.build(), defaults);
        assertCriteria(result,
                Arrays.asList("c", "a", "b"),
                3, 1, 2);
    }

    @Test
    public void testCriteriaGrouping() {
        Builder query = a2j().addCriteria("a", 1)
                .addCriteria("a", 2);
        List<ResourceQueryCriteria> result = criteria(query.build(), null);
        assertCriteria(result,
                Arrays.asList("a"),
                Arrays.asList(Operator.in),
                Arrays.asList(1, 2));

        ResourceQueryDefaults defaults = new ResourceQueryDefaults(a2j, query.build());
        defaults.addCriteria("c", 3);
        result = criteria(query.build(), defaults);
        assertCriteria(result,
                Arrays.asList("c", "a"),
                Arrays.asList(Operator.eq, Operator.in),
                3, Arrays.asList(1, 2));
    }

    private void assertCriteria(List<ResourceQueryCriteria> result, List<String> paths, Object... values) {
        List<Operator> operators = new ArrayList<>();
        for (String path : paths) {
            operators.add(Operator.eq);
        }
        assertCriteria(result, paths, operators, values);
    }

    private void assertCriteria(List<ResourceQueryCriteria> result, List<String> paths, List<Operator> operators, Object... values) {
        assertEquals(paths.size(), result.size());
        int i = 0;
        for (ResourceQueryCriteria c : result) {
            ResourcePathFilter<?> f = (ResourcePathFilter<?>) c;
            assertEquals(paths.get(i), f.getPath().getBeanPath());
            assertEquals(operators.get(i), f.getOperator());
            assertEquals(values[i], f.getValue());
            i++;
        }
    }

    @Test
    public void testDefaultFields() {
        Builder query = a2j();
        List<ResourcePath> paths = sparseFields(a2j, query.build());
        assertBeanPaths(paths, "id", "a", "b", "c", "d", "e", "f", "g", "h");

        paths = queryFields(a2j, query.build(), null);
        assertBeanPaths(paths, "id", "a", "b", "c", "d", "e", "f", "g", "h");
    }

    @Test
    public void testEvery() {
        Builder query = a2j(Type.Every);
        List<ResourcePath> paths = sparseFields(a2j, query.build());
        assertBeanPaths(paths, "id", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j");

        paths = queryFields(a2j, query.build(), null);
        assertBeanPaths(paths, "id", "a", "b", "c", "d", "e", "f", "g", "h");
    }

    @Test
    public void testSparseFields() {
        Builder query = a2j(Type.Sparse, "a", "b", "c");
        List<ResourcePath> paths = sparseFields(a2j, query.build());
        assertBeanPaths(paths, "a", "b", "c");
    }

    @Test
    public void testInclusion() {
        testInclusion(false);
    }

    @Test
    public void testInclusionRestricted() {
        testInclusion(true);
    }

    public void testInclusion(boolean restricted) {
        Builder query = a2j().addRequestedPathsAdded("d", "j");
        ResourceQueryStatement q = query.build();
        ResourceQueryDefaults defaults = new ResourceQueryDefaults(a2j, q);
        sparseFieldsDefaultsProvider.paths = Arrays.asList(
                path(a2j, "a"),
                path(a2j, "b"),
                path(a2j, "c")
        );
        defaults.addRequiredFields("e");
        if (restricted) {
            restrictedFieldsProvider.paths = Arrays.asList(
                    path(a2j, "d")
            );
        }

        List<ResourcePath> paths = sparseFields(a2j, q);
        if (restricted) {
            assertBeanPaths(paths, "a", "b", "c", "j");
        } else {
            assertBeanPaths(paths, "a", "b", "c", "d", "j");
        }

        paths = queryFields(a2j, q, defaults);
        if (restricted) {
            assertBeanPaths(paths, "id", "a", "b", "c", "e");
        } else {
            assertBeanPaths(paths, "id", "a", "b", "c", "d", "e");
        }
    }

    final class TestResourcePathsProvider implements ResourcePathsProvider {

        List<ResourcePath> paths;
        ResourcePathsProvider delegate;

        @Override
        public List<ResourcePath> getPaths(Resource<?, ?> resource) {
            if (paths != null) {
                return paths;
            }
            if (delegate != null) {
                return delegate.getPaths(resource);
            }
            return null;
        }

    }
}
