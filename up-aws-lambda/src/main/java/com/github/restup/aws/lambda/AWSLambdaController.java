package com.github.restup.aws.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.restup.controller.ResourceController;
import com.github.restup.errors.RequestError;
import com.github.restup.jackson.service.model.JacksonRequestBody;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.service.model.ResourceData;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AWSLambdaController implements
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final static Logger log = LoggerFactory.getLogger(AWSLambdaController.class);

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
        AWSLambdaResourceControllerResponse lambdaResponse = new AWSLambdaResourceControllerResponse();

        try {
            Object response = controller
                .request(APIGatewayProxyEventResourceControllerRequest.builder(event)
                    .body(getBody(event))
                    .registry(registry)
                    .baseRequestUrl(baseRequestUrl), lambdaResponse);

            lambdaResponse.setBody(toJson(response));
        } catch (Exception e) {
            log.error("Unexpected exception", e);

            RequestError error = RequestError.error(null, e).build();
            try {
                lambdaResponse.setBody(toJson(error));
            } catch (Exception e1) {
                log.error("Unable to serialize error response", e1);
                lambdaResponse.setBody("Unable to serialize");
            }
        }

        return lambdaResponse;
    }

    private String toJson(Object result) throws JsonProcessingException {
        if (result == null) {
            return null;
        }
        if (result instanceof String) {
            return (String) result;
        }
        return mapper.writeValueAsString(result);
    }

    private ResourceData<?> getBody(APIGatewayProxyRequestEvent event) throws IOException {
        JacksonRequestBody body = null;
        if (StringUtils.isNotEmpty(event.getBody())) {
                body = mapper.readValue(event.getBody(), JacksonRequestBody.class);
        }
        return body;
    }

}
