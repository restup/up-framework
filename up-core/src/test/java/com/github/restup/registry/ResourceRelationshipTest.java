package com.github.restup.registry;

import com.university.Course;
import com.university.University;
import com.github.restup.annotations.field.RelationshipType;
import com.github.restup.path.ResourcePath;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ResourceRelationshipTest {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testGetIds() {
        ResourceRegistry registry = ResourceRegistryTest.registry();
        registry.registerResource(University.class, Course.class);
        ResourceRelationship<Course, Long, University, Long> relationship = (ResourceRelationship) registry.getRelationship("university", "course");

        assertEquals(Sets.newSet(1l), relationship.getIdsTo(u(1)));
        assertEquals(Sets.newSet(1l, 2l, 3l), relationship.getIdsTo(Arrays.asList(u(1), u(2), u(3), u(2))));

        assertEquals(Sets.newSet(1l), relationship.getIds(c(1)));
        assertEquals(Sets.newSet(1l, 2l, 3l), relationship.getIds(Arrays.asList(c(1), c(2), c(3), c(2))));
    }

    private University u(long i) {
        University u = new University();
        u.setId(i);
        return u;
    }

    private Course c(long i) {
        Course c = new Course();
        c.setUniversityId(i);
        return c;
    }

    @Test
    public void testManyToOne() {
        // confirm order resources registered doesn't matter
        ResourceRegistry b = ResourceRegistryTest.registry();
        b.registerResource(Course.class);
        b.registerResource(University.class);
        assertCourseToUniversity(b);

        ResourceRegistry a = ResourceRegistryTest.registry();
        a.registerResource(University.class);
        a.registerResource(Course.class);
        assertCourseToUniversity(a);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void assertCourseToUniversity(ResourceRegistry registry) {
        ResourceRelationship relationship = registry.getRelationship("university", "course");
        assertNotNull(relationship);
        Resource university = registry.getResource(University.class);
        Resource course = registry.getResource(Course.class);
        assertEquals(course, relationship.getFrom());
        assertEquals(university, relationship.getTo());
        assertEquals(RelationshipType.manyToOne, relationship.getType(course));
        assertEquals(RelationshipType.oneToMany, relationship.getType(university));
        assertEquals(Arrays.asList(ResourcePath.path(course, "universityId")), relationship.getFromPaths());
        assertEquals(Arrays.asList(ResourcePath.path(university, "id")), relationship.getToPaths());
    }

}
