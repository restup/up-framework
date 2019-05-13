package com.github.restup.repository.dynamodb.mapping;

import static com.github.restup.repository.dynamodb.mapping.DynamoDBFieldBuilderVisitor.PRIMARY_KEY;
import static com.github.restup.util.UpUtils.mapOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderVisitor;
import com.github.restup.mapping.fields.MappedIndexField;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.repository.collections.MapBackedRepositoryFactory;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class DynamoDBFieldBuilderVisitorTest {

    private ResourceRegistry registry;


    @Before
    public void before() {
        registry =
            ResourceRegistry.builder()
                .repositoryFactory(new MapBackedRepositoryFactory())
                .mappedFieldVisitorBuilder(
                    MappedFieldBuilderVisitor.builder().withIdentityConvention("id")
                        .add(new DynamoDBFieldBuilderVisitor())
                ).build();

        registry.registerResources(OverIndexedTable.class);
    }

    @Test
    public void testOverIndexedTable() {
        Resource resource = (Resource) registry.getResource("oit");
        assertEquals(6, resource.getAllPaths().size());
        assertNull(resource.findPersistedField("ignoreme").getPersistedName());
        assertNotNull(resource.findPersistedField("foobar"));
        assertEquals("id", resource.getIdentityField().getPersistedName());
        assertIndexes(resource, "id", (Map) mapOf(PRIMARY_KEY, 0));
        assertIndexes(resource, "foo", (Map) mapOf("idx1", 0, "idx3", 0, "idx2", 1, "idx4", 1));
        assertIndexes(resource, "bar", (Map) mapOf("idx2", 0, "idx1", 1));
        assertIndexes(resource, "baz", (Map) mapOf("idx4", 0, "idx3", 1));
        assertIndexes(resource, "foobar", Collections.emptyMap());
    }

    private void assertIndexes(Resource resource, String field,
        Map<String, Integer> expected) {
        MappedField mf = resource.findPersistedField(field);
        Set<MappedIndexField> indexes = mf.getIndexes();
        for (MappedIndexField idx : indexes) {
            Integer position = expected.get(idx.getIndexName());
            assertNotNull(
                "Found unexpected index named " + idx.getIndexName() + " for field" + field,
                position);
            assertEquals(
                "Incorrect position for index " + idx.getIndexName() + " for field" + field,
                position.shortValue(),
                idx.getPosition().shortValue());
        }
        assertEquals(expected.size(), indexes.size());
    }
}
