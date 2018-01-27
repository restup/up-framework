package com.github.restup.path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import java.util.Collections;
import org.junit.Test;
import com.github.restup.registry.Resource;

public class EmptyResourcePathsProviderTest {

    @Test
    public void testGetPaths() {
        EmptyResourcePathsProvider provider = new EmptyResourcePathsProvider();
        assertEquals(Collections.emptyList(), provider.getPaths(mock(Resource.class)));
    }
}
