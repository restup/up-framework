package com.github.restup.aws.lambda.support;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.restup.aws.lambda.AWSLambdaController;
import com.github.restup.controller.ResourceController;
import com.github.restup.jackson.JacksonConfiguration;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.test.ApiExecutor;
import com.github.restup.test.ApiRequest;
import com.github.restup.test.ApiResponse;
import com.github.restup.test.BasicApiResponse;
import com.github.restup.test.resource.Contents;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mock {@link ApiExecutor} for testing {@link ResourceController} directly outside of a container
 * or other framework support
 */
public class AWSLambdaApiExecutor implements ApiExecutor {

    private final static Logger log = LoggerFactory.getLogger(AWSLambdaApiExecutor.class);

    private final ResourceRegistry registry;
    private final ResourceController controller;

    public AWSLambdaApiExecutor(ResourceRegistry registry, ResourceController controller) {
        this.registry = registry;
        this.controller = controller;
    }

    @Override
    public ApiResponse<String[]> execute(ApiRequest request) {

        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();

        Map<String, List<String>> multiValueHeaders = new HashMap<>();
        Map<String, String> headers = new HashMap<>();

        addAll(request.getHeaders(), headers, multiValueHeaders);

        event.setHeaders(headers);
        event.setMultiValueHeaders(multiValueHeaders);

        if (request.getBody() != null) {
            event.setBody(request.getBody().getContentAsString());
        }
        event.setHttpMethod(request.getMethod().name());

        event.setPath(request.getPath());

        Map<String, String> params = new HashMap<>();
        Map<String, List<String>> multiValueParams = new HashMap<>();
        addAll(request.getParams(), params, multiValueParams);
        event.setQueryStringParameters(params);
        event.setMultiValueQueryStringParameters(multiValueParams);

        AWSLambdaController lambdaController = new AWSLambdaController(registry, controller,
            JacksonConfiguration.configure(), "http://localhost");

        Context context = null;
        APIGatewayProxyResponseEvent result = lambdaController.handleRequest(event, context);

        if (log.isDebugEnabled()) {
            log.debug("\n\nRequest:\n{} {}"
                    + "\n\nResponse:\n{}\n\n"
                , request.getMethod(), request.getUrl(),
                result.getBody());
        }

        Map<String, String[]> resultHeaders = new HashMap<>();
        for (Entry<String, String> e : result.getHeaders().entrySet()) {
            resultHeaders.put(e.getKey(), new String[]{e.getValue()});
        }

        return new BasicApiResponse(result.getStatusCode(), resultHeaders,
            Contents.of(result.getBody()));
    }

    private void addAll(Map<String, String[]> source, Map<String, String> target,
        Map<String, List<String>> multiValueTarget) {
        for (Entry<String, String[]> e : source.entrySet()) {
            if (e.getValue() != null) {
                for (String value : e.getValue()) {
                    target.put(e.getKey(), value);
                }
                multiValueTarget.put(e.getKey(), Arrays.asList(e.getValue()));
            }
        }
    }

}
