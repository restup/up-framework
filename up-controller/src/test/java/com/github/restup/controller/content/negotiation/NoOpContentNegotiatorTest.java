package com.github.restup.controller.content.negotiation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.github.restup.controller.model.ParsedResourceControllerRequest;

@RunWith(MockitoJUnitRunner.class)
public class NoOpContentNegotiatorTest {

    @Mock
    ParsedResourceControllerRequest<?> details;

    @Test
    public void testAccept() {
        assertTrue(new NoOpContentNegotiator().accept(details));
        assertFalse(new NoOpContentNegotiator().accept(null));
    }

    @Test
    public void testFormatResponse() {
        String foo = "foo";
        assertSame(foo, new NoOpContentNegotiator().formatResponse(null, null, foo));
    }
}
