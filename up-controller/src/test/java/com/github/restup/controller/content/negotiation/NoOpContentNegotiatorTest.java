package com.github.restup.controller.content.negotiation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NoOpContentNegotiatorTest {

    @Mock
    ParsedResourceControllerRequest<?> details;

    @Test
    public void testAccept() {
        assertTrue(new NoOpContentNegotiator().accept(details));
        assertFalse(new NoOpContentNegotiator().accept(null));
    }
}
