package com.github.restup.test.serializer;

import static org.junit.Assert.assertEquals;
import java.lang.reflect.Type;
import java.util.HashMap;
import org.junit.Test;
import com.github.restup.test.assertions.Assertions;
import com.github.restup.test.model.Foo;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class GsonSerializerTest {

    private GsonSerializer serializer = new GsonSerializer();

    @Test
    public void testSerialize() {
        assertEquals("{}", serializer.convertToString(new HashMap<>()));
        assertEquals("{}", serializer.convertToString(new Foo()));
    }

    @Test
    public void testSerializeException() {

        GsonBuilder gson = new GsonBuilder();
        gson.registerTypeAdapter(Foo.class, new FooSerializer());
        GsonSerializer serializer = new GsonSerializer(gson);
        
        Assertions.assertThrows(() -> serializer.convertToString(new Foo()), AssertionError.class);

    }



    public static class FooSerializer implements JsonSerializer<Foo> {

        @Override
        public JsonElement serialize(Foo arg0, Type arg1, JsonSerializationContext arg2) {
            throw new NullPointerException();
        }
    }

}
