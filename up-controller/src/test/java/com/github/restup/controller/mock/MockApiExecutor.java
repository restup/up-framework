package com.github.restup.controller.mock;

import static com.github.restup.controller.mock.MockResourceControllerRequest.getUrl;

import com.github.restup.controller.ResourceController;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.ResourceData;
import com.github.restup.test.ApiExecutor;
import com.github.restup.test.ApiRequest;
import com.github.restup.test.ApiResponse;
import com.github.restup.test.BasicApiResponse;
import com.github.restup.test.resource.Contents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mock {@link ApiExecutor} for testing {@link ResourceController} directly outside of a container or other framework support
 */
public class MockApiExecutor implements ApiExecutor {

    private final static Logger log = LoggerFactory.getLogger(MockApiExecutor.class);

    private final ResourceRegistry registry;
    private final ResourceController controller;
    private final MockContentNegotiation contentNegotiation;

    public MockApiExecutor(ResourceRegistry registry, ResourceController controller, MockContentNegotiation contentNegotiation) {
        this.registry = registry;
        this.controller = controller;
        this.contentNegotiation = contentNegotiation;
    }

    @Override
    public ApiResponse<String[]> execute(ApiRequest request) {

        MockResourceControllerRequest.Builder mockRequestBuilder 
        		= MockResourceControllerRequest.builder()
        			.method(request.getMethod().name())
        			.url(request.getUrl())
        			.headers(request.getHeaders())
            .registry(registry);

        ResourceData<?> body = contentNegotiation.getBody(request.getBody());
        mockRequestBuilder.body(body);

        MockResourceControllerResponse mockResponse = new MockResourceControllerResponse();
        Object result = null;
        try {
            result = controller.request(mockRequestBuilder, mockResponse);
        } catch ( Throwable t) {
            result = controller.handleException(mockRequestBuilder.getResult(), mockResponse, t);
        }

        Contents resultContents = serialize(mockRequestBuilder.getResult(), mockResponse, result);

        if (log.isDebugEnabled()) {
            log.debug("\n\nRequest:\n{} {}"
                            + "\n\nResponse:\n{}\n\n"
                    , request.getMethod(), getUrl(request.getUrl()),
                    resultContents.getContentAsString());
        }

        return new BasicApiResponse<>(mockResponse.getStatus(), mockResponse.getHeaders(),
            resultContents);
    }

    private Contents serialize(MockResourceControllerRequest mockRequest, MockResourceControllerResponse mockResponse, Object result) {
        try {
            return contentNegotiation.serialize(result);
        } catch (Exception e) {
            try {
                return contentNegotiation.serialize(controller.handleException(mockRequest, mockResponse, e));
            } catch (Exception e1) {
                throw new AssertionError("Unable to serialize response", e1);
            }
        }
    }

}
