package com.github.restup.aws.lambda;

import static com.github.restup.util.UpUtils.nvl;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.github.restup.controller.model.ResourceControllerResponse;
import java.util.HashMap;

public class AWSLambdaResourceControllerResponse extends APIGatewayProxyResponseEvent implements
    ResourceControllerResponse {

    public AWSLambdaResourceControllerResponse() {
        setHeaders(new HashMap<>());
        setIsBase64Encoded(false);
    }

    @Override
    public void setHeader(String name, String value) {
        getHeaders().put(name, value);
    }

    @Override
    public int getStatus() {
        return nvl(getStatusCode(), 200);
    }

    @Override
    public void setStatus(int status) {
        setStatusCode(status);
    }
}
