package com.github.restup.annotations.field;

import static com.github.restup.annotations.field.RelationshipType.manyToMany;
import static com.github.restup.annotations.field.RelationshipType.manyToOne;
import static com.github.restup.annotations.field.RelationshipType.oneToMany;
import static com.github.restup.annotations.field.RelationshipType.oneToOne;
import static org.junit.Assert.assertEquals;
import java.util.function.Function;
import org.junit.Test;

public class RelationshipTypeTest {
    
    @Test
    public void testIsOneTo() {
        assertType(true, RelationshipType::isOneTo, oneToOne, oneToMany);
        assertType(false, RelationshipType::isOneTo, manyToOne, manyToMany);
    }
    
    @Test
    public void testIsManyTo() {
        assertType(true, RelationshipType::isManyTo, manyToOne, manyToMany);
        assertType(false, RelationshipType::isManyTo, oneToOne, oneToMany);
    }
    
    @Test
    public void testToOne() {
        assertType(true, RelationshipType::isToOne, oneToOne, manyToOne);
        assertType(false, RelationshipType::isToOne, oneToMany, manyToMany);
    }
    
    @Test
    public void testToMany() {
        assertType(true, RelationshipType::isToMany, oneToMany, manyToMany);
        assertType(false, RelationshipType::isToMany, oneToOne, manyToOne);
    }
    
    private static void assertType(boolean expected, Function<RelationshipType, Boolean> f, RelationshipType... relationshipTypes ) {
        for ( RelationshipType type : relationshipTypes ) {
            assertEquals(expected, f.apply(type));
        }
    }

}
