package com.github.restup.controller.model;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.Test;

public class BasicResourceControllerRequestTest {

    @Test
    public void testGetHeaders() {
        BasicResourceControllerRequest request = mock(BasicResourceControllerRequest.class);
        when(request.getHeaders("foo")).thenCallRealMethod();
        assertNull(request.getHeaders("foo"));
    }

}
