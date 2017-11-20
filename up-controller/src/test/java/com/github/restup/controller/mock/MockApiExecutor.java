package com.github.restup.controller.mock;

import com.github.restup.controller.ResourceController;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.ResourceData;
import com.github.restup.test.ApiExecutor;
import com.github.restup.test.ApiRequest;
import com.github.restup.test.ApiResponse;
import com.github.restup.test.RpcApiTest;
import com.github.restup.test.resource.Contents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.restup.controller.mock.MockResourceControllerRequest.getUrl;

/**
 * Mock {@link ApiExecutor} for testing {@link ResourceController} directly outside
 * of a container or other framework support
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

    public ApiResponse<String[]> execute(RpcApiTest settings) {
        ApiResponse.Builder response = ApiResponse.builder();

        MockResourceControllerRequest.Builder mockRequest = MockResourceControllerRequest.builder();
        ApiRequest request = settings.getRequest();

        mockRequest.method(request.getMethod().name());
        mockRequest.url(request.getUrl());
        mockRequest.headers(request.getHeaders());
        mockRequest.setRegistry(registry);

        ResourceData body = contentNegotiation.getBody(request.getBody());
        mockRequest.setBody(body);

        MockResourceControllerResponse mockResponse = new MockResourceControllerResponse();
        Object result = controller.request(mockRequest, mockResponse);


        Contents resultContents = serialize(mockResponse, result);

        if ( log.isDebugEnabled() ) {
            log.debug("\n\nRequest:\n{} {}"
                            + "\n\nResponse:\n{}\n\n"
                    , request.getMethod(), getUrl(request.getUrl()),
                resultContents.getContentAsString());
        }

        return new ApiResponse<String[]>(mockResponse.getStatus(), mockResponse.getHeaders(), resultContents);
    }

    private Contents serialize(MockResourceControllerResponse mockResponse, Object result) {
        try {
            return contentNegotiation.serialize(result);
        } catch (Exception e) {
            try {
                return contentNegotiation.serialize(controller.handleException(mockResponse, e));
            } catch (Exception e1) {
                throw new RuntimeException("Unable to serialize response", e1);
            }
        }
    }

}
