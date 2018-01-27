package com.test.model;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import com.deep.Shallow;
import com.many.fields.A2J;

public class DeepTest {
    
    @Test
    public void deep() {
        Shallow graph = Shallow.graph();
        assertNotNull(graph);
    }
    
    @Test
    public void a2j() {
        new A2J(1l,"a","b","c","d","e","f","g","h","i","j");
    }

}
