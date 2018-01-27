package com.github.restup.controller.model.result;

import static org.mockito.Mockito.*;
import org.junit.Test;
import static org.junit.Assert.*;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;

public class NegotiatedResultTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testGetData() {
        ParsedResourceControllerRequest<?> request = mock(ParsedResourceControllerRequest.class);
        when(request.getResource()).thenReturn(mock(Resource.class));
        NegotiatedResult result = new JsonResult(request, "foo");
        assertEquals("foo", result.getData());
    }
    
}
