package com.github.restup.controller.content.negotiation;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collections;
import org.junit.Test;
import com.github.restup.controller.model.MediaType;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.controller.model.result.HalJsonResult;
import com.github.restup.registry.Resource;

public class HalJsonContentNegotiatorTest {


    @Test
    public void testFormatResponse() {
        String foo = "foo";
        ResourceControllerResponse response = mock(ResourceControllerResponse.class);

        ParsedResourceControllerRequest<?> request = mock(ParsedResourceControllerRequest.class);
        when(request.getRequestedQueries()).thenReturn(Collections.emptyList());
        when(request.getResource()).thenReturn(mock(Resource.class));
        HalJsonResult result = (HalJsonResult) new HalJsonContentNegotiator().formatResponse(request, response, foo);
        assertSame(foo, result.getResult());
        verify(response).setHeader(ContentTypeNegotiation.CONTENT_TYPE, MediaType.APPLICATION_JSON_HAL.getContentType());
    }
}
