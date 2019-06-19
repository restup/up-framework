package com.github.restup.path;

import static com.github.restup.util.TestRegistries.defaultRegistry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.github.restup.service.model.request.BasicUpdateRequest;
import com.model.test.company.Company;
import com.model.test.company.Person;
import com.music.Label;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

public class ResourcePathTest {

    private ResourcePath path(Class<?> c, String path) {
        return builder(c)
                .setQuiet(true).path(path).build();
    }

    private ResourcePath.Builder builder(Class<?> c) {
        return ResourcePath.builder(defaultRegistry(), c);
    }

    @Test
    public void testNestedArrays() {
        ResourcePath data = builder(Label.class).data(0).path("albums.0.tracks.0.number").build();
        assertEquals("/data/0/albums/0/tracks/0/number", data.getSource());
        assertTrue(data.isValid());

        data = builder(Label.class).data(0).path("albums.0.artist.name").build();
        assertEquals("/data/0/albums/0/artist/name", data.getSource());
        assertTrue(data.isValid());
    }

    @Test
    public void testPersistedPath() {
        ResourcePath path = path(Person.class, "address.street");
        assertEquals("address.street", path.getPersistedPath());
        ResourcePath data = builder(Person.class).data().path("address.street").build();
        assertEquals("address.street", data.getPersistedPath());
    }

    @Test
    public void testPath() {
        ResourcePath path = path(Person.class, "firstName");
        assertNull(path.prior());
        assertPath("firstName", path.value());
        assertNull(path.next());
        assertTrue(path.isValid());
    }

    @Test
    public void testEmbeddedPath() {
        ResourcePath path = path(Person.class, "address.street");
        assertNull(path.prior());
        assertPath("address", path.value());
        assertPath("street", path.next().value());
        assertNull(path.next().next());
        assertTrue(path.isValid());

        ResourcePath data = builder(Person.class).data().path("address.street").build();
        assertNull(data.prior());
        assertPath("data", data.value());
        assertPath("address", data.next().value());
        assertPath("street", data.next().next().value());
        assertNull(data.next().next().next());
        assertTrue(data.isValid());

        ResourcePath indexed = builder(Person.class).data(1).path("address.street").build();
        assertNull(indexed.prior());
        assertPath("data", indexed.value());
        assertPath("1", indexed.next().value());
        assertPath("address", indexed.next().next().value());
        assertPath("street", indexed.next().next().next().value());
        assertNull(indexed.next().next().next().next());
        assertTrue(indexed.isValid());

    }

    @Test
    public void testEquals() {
        ResourcePath path = path(Person.class, "address.street");
        ResourcePath data = builder(Person.class).data().path("address.street").build();
        ResourcePath indexed = builder(Person.class).data(1).path("address.street").build();

        // basic
        assertFalse(path.equals(data));
        assertFalse(path.equals(indexed));
        assertFalse(data.equals(indexed));
        // path portions are equal, but full path not the same
        assertFalse(path.equals(data.next()));
        assertFalse(path.equals(indexed.next().next()));
        //  paths are equal
        assertTrue(path.equalsPath(data.next()));
        assertTrue(path.equalsPath(indexed.next().next()));

        HashSet<ResourcePath> set = new HashSet<>();
        set.add(path);
        set.add(data);
        set.add(indexed);
        assertEquals(3, set.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIndex() {
        builder(Person.class).data(-1);
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidIndexLocation() {
        builder(Person.class).path("address.street").index(1);
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidDataLocation() {
        builder(Person.class).path("address").data();
    }

    @Test(expected = IllegalStateException.class)
    public void testMappedClassNotset() {
        builder(null).path("address");
    }

    @Test
    public void testInvalid() {
        ResourcePath path = path(Person.class, "fName");
        assertNull(path.prior());
        assertPath("fName", path.value());
        assertNull(path.next());
        assertFalse(path.isValid());
    }

    @Test
    public void testInvalidEmbedded() {
        ResourcePath path = path(Person.class, "address.street1");
        assertNull(path.prior());
        assertPath("address", path.value());
        assertPath("street1", path.next().value());
        assertNull(path.next().next());
        assertFalse(path.isValid());
    }

    @Test
    public void testReadValue() {
        ResourcePath path = path(Person.class, "address.street");
        testReadWrite(path, new Person(), "foo");
    }

    @Test
    public void testReadDataValue() {
        ResourcePath data = builder(Person.class).data().path("address.street").build();
        testReadWrite(data,
            new BasicUpdateRequest(null, null, new Person(), null, null, null, null), "bar");
    }

    @Test
    public void testReadDataIndexedValue() {
        ResourcePath indexed = builder(Person.class).data(0).path("address.street").build();
        testReadWrite(indexed,
            new BasicUpdateRequest(null, null, Arrays.asList(new Person()), null, null, null, null),
            "foo");
    }

    @Test
    public void testReadCollection() {
        ResourcePath workers = builder(Company.class).data(0).path("workers").build();
        Company c = new Company();
        c.setWorkers(Arrays.asList(1l, 2l, 3l));
        assertEquals(c.getWorkers(), workers.getValue(c));

        Company b = new Company();
        b.setWorkers(Arrays.asList(1l, 3l, 5l));
        assertEquals(Arrays.asList(1l, 3l, 5l), workers.getValue(Arrays.asList(b, c)));

        workers = builder(Company.class).data(1).path("workers").build();
        assertEquals(Arrays.asList(1l, 2l, 3l), workers.getValue(Arrays.asList(b, c)));

        workers = builder(Company.class).path("workers").build();
        assertEquals(Arrays.asList(1l, 3l, 5l, 1l, 2l, 3l), workers.getValue(Arrays.asList(b, c)));
        assertEquals(Sets.newSet(1l, 2l, 3l, 5l),
            workers.collectValues(new HashSet<>(), Arrays.asList(b, c)));
    }

    private void testReadWrite(ResourcePath path, Object target, String value) {
        path.setValue(target, value);
        assertEquals(value, path.getValue(target));
    }

    private void assertPath(String field, PathValue value) {
        assertEquals(field, value.getBeanPath());
        assertEquals(field, value.getApiPath());
        assertEquals(field, value.getPersistedPath());
        assertEquals(field, value.toString());
    }

}
