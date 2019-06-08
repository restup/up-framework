package com.github.restup.aws.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.ResourceController;
import com.github.restup.jackson.service.model.JacksonRequestBody;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.ResourceData;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class AWSLambdaController implements
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ResourceRegistry registry;

    private final ResourceController controller;
    private final ObjectMapper mapper;
    private final String baseRequestUrl;


    public AWSLambdaController(ResourceRegistry registry,
        ResourceController controller, ObjectMapper mapper, String baseRequestUrl) {
        this.registry = registry;
        this.controller = controller;
        this.mapper = mapper;
        this.baseRequestUrl = baseRequestUrl;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event,
        Context context) {
        Map<String, String> map = new HashMap<>();

        AWSLambdaResourceControllerResponse lambdaResponse = new AWSLambdaResourceControllerResponse();

        Object response = controller
            .request(APIGatewayProxyEventResourceControllerRequest.builder(event)
                .body(getBody(event))
                .registry(registry)
                .baseRequestUrl(baseRequestUrl), lambdaResponse);

        lambdaResponse.setBody(toJson(response));

        return lambdaResponse;
    }

    private String toJson(Object result) {
        if (result instanceof String) {
            return (String) result;
        }
        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            //TODO  handle exceptions
            e.printStackTrace();
            return "ex";
        }
    }

    private ResourceData<?> getBody(APIGatewayProxyRequestEvent event) {
        JacksonRequestBody body = null;
        if (StringUtils.isNotEmpty(event.getBody())) {
            try {
                body = mapper.readValue(event.getBody(), JacksonRequestBody.class);
            } catch (IOException e) {
                //TODO  handle exceptions
                e.printStackTrace();
            }
        }
        return body;
    }

}
