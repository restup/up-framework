package com.github.restup.test.serializer;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import org.junit.Test;
import com.github.restup.test.assertions.Assertions;
import com.github.restup.test.model.Foo;
import com.github.restup.test.model.ImmutableFoo;

public class JacksonSerializerTest {
    
    JacksonSerializer serializer = new JacksonSerializer();

    @Test
    public void testSerialize() {
        assertEquals("{}",serializer.convertToString(new HashMap<>()));
        assertEquals("{\"name\":\"foo\"}",serializer.convertToString(new ImmutableFoo("foo")));
    }

    @Test
    public void testSerializeException() {
        Assertions.assertThrows(()-> serializer.convertToString(new Foo())
                , AssertionError.class);
    }
    
}
