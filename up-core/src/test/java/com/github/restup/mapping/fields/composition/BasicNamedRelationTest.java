package com.github.restup.mapping.fields.composition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BasicNamedRelationTest {


    @Test
    public void testResourceName() {
        BasicNamedRelation relation = (BasicNamedRelation) Relation.builder().name("bar").resource("foo").build();
        assertEquals("foo", relation.getResourceName());
        assertEquals("foo", relation.getResource(null));
        assertEquals("bar", relation.getName());
        assertTrue(relation.isIncludable());
        assertTrue(relation.isValidateReferences());
    }

}
