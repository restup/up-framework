package com.github.restup.util;

import com.github.restup.path.ResourcePath;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

public class ResourceAssert {

    private ResourceAssert() {

    }

    public static void assertPaths(List<ResourcePath> result, String... paths) {
        assertThat((Collection<ResourcePath>) result, hasSize(paths.length));
        for (int i = 0; i < paths.length; i++) {
            assertEquals(paths[i], result.get(i).getSource());
        }
    }

    public static void assertBeanPaths(List<ResourcePath> result, String... paths) {
        assertThat((Collection<ResourcePath>) result, hasSize(paths.length));
        for (int i = 0; i < paths.length; i++) {
            assertEquals(paths[i], result.get(i).getBeanPath());
        }
    }

}
