package com.github.restup.controller.content.negotiation;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.controller.model.result.JsonApiResult;
import com.github.restup.test.assertions.Assertions;

@RunWith(MockitoJUnitRunner.class)
public class DefaultContentNegotiatorTest {

    @Mock
    ContentNegotiator contentNegotiator;

    @Test
    public void testAccept() {
        DefaultContentNegotiator defaultContentNegotiator = new DefaultContentNegotiator(contentNegotiator);
        ResourceControllerRequest request = mock(ResourceControllerRequest.class);
        assertTrue(defaultContentNegotiator.accept(request));
        verifyZeroInteractions(contentNegotiator, request);
    }

    @Test
    public void testFormatResponse() {
        DefaultContentNegotiator defaultContentNegotiator = new DefaultContentNegotiator(contentNegotiator);
        ParsedResourceControllerRequest<?> request = mock(ParsedResourceControllerRequest.class);
        ResourceControllerResponse response = mock(ResourceControllerResponse.class);
        JsonApiResult result = mock(JsonApiResult.class);
        defaultContentNegotiator.formatResponse(request, response, result);
        verify(contentNegotiator).formatResponse(request, response, result);
        verifyZeroInteractions(contentNegotiator, request);
    }

    @Test
    public void testGetContentNegotiator() {
        Assertions.assertThrows(() -> DefaultContentNegotiator.getContentNegotiator("text/foo", contentNegotiator), IllegalArgumentException.class);
    }

}
