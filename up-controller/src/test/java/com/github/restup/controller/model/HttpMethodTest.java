package com.github.restup.controller.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.*;

import com.github.restup.registry.settings.ControllerMethodAccess;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class HttpMethodTest {

    ControllerMethodAccess disabled = ControllerMethodAccess.builder().setAllDisabled(true).build();
    ControllerMethodAccess enabled = ControllerMethodAccess.builder().setAllDisabled(false).build();

    @Test
    public void testSupportsAccessByIds() {
        List<HttpMethod> supported = Arrays.asList(HttpMethod.GET, HttpMethod.PATCH, HttpMethod.DELETE);
        for (HttpMethod m : HttpMethod.values()) {
            assertFalse(m.supportsAccessByIds(disabled));
            if (supported.contains(m)) {
                assertTrue(m.supportsAccessByIds(enabled));
            } else {
                assertFalse(m.supportsAccessByIds(enabled));
            }
        }
    }

    @Test
    public void testSupportsItemOperation() {
        List<HttpMethod> supported = Arrays.asList(HttpMethod.GET, HttpMethod.PATCH, HttpMethod.DELETE, HttpMethod.PUT);
        for (HttpMethod m : HttpMethod.values()) {
            assertFalse(m.supportsItemOperation(disabled));
            if (supported.contains(m)) {
                assertTrue(m.supportsItemOperation(enabled));
            } else {
                assertFalse(m.supportsItemOperation(enabled));
            }
        }
    }

    @Test
    public void testSupportsCollectionOperation() {
        List<HttpMethod> supported = Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PATCH, HttpMethod.DELETE);
        for (HttpMethod m : HttpMethod.values()) {
            assertFalse(m.supportsCollectionOperation(disabled));
            if (supported.contains(m)) {
                assertTrue(m.supportsCollectionOperation(enabled));
            } else {
                assertFalse(m.supportsCollectionOperation(enabled));
            }
        }
    }

    @Test
    public void testSupportsMultiple() {
        List<HttpMethod> supported = Arrays.asList(HttpMethod.PATCH, HttpMethod.PUT, HttpMethod.POST);
        for (HttpMethod m : HttpMethod.values()) {
            assertFalse(m.supportsMultiple(disabled));
            if (supported.contains(m)) {
                assertTrue(m.supportsMultiple(enabled));
            } else {
                assertFalse(m.supportsMultiple(enabled));
            }
        }
    }
    
    @Test
    public void testOf() {
        assertNull(HttpMethod.of("foo"));
        for (HttpMethod m : HttpMethod.values()) {
            assertEquals(m, HttpMethod.of(m.name().toLowerCase()));
            assertEquals(m, HttpMethod.of(m.name().toUpperCase()));
        }
    }
}
