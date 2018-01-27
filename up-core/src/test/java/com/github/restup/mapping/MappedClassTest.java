package com.github.restup.mapping;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import java.util.Map;
import org.junit.Test;

public class MappedClassTest {
    
    @Test
    public void testAnonymousMapping() {
        MappedClass<?> mappedClass = MappedClass.builder()
                .name("testResource")
            .addAttribute(String.class, "foo").build();
        assertEquals(1, mappedClass.getAttributes().size());
        
        assertThat(mappedClass.newInstance(), instanceOf(Map.class));
    }

}
