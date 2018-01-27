package com.github.restup.mapping;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import java.util.HashMap;
import java.util.TreeMap;
import org.junit.Test;

public class UntypedClassTest {

    @Test
    public void testNewInstance() {
        assertThat(new UntypedClass<>(TreeMap.class).newInstance(), instanceOf(TreeMap.class));
        assertThat(new UntypedClass<>().newInstance(), instanceOf(HashMap.class));
    }
    
}
