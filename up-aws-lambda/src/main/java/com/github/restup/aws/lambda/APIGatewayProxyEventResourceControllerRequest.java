package com.github.restup.aws.lambda;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.github.restup.controller.content.negotiation.ContentTypeNegotiation;
import com.github.restup.controller.model.AbstractResourceControllerRequestBuilder;
import com.github.restup.controller.model.BasicResourceControllerRequest;
import com.github.restup.controller.model.HttpMethod;
import com.github.restup.service.model.ResourceData;
import java.util.ArrayList;
import java.util.List;

/**
 * Wraps {@link APIGatewayProxyRequestEvent} to pass parameter details for Filtereds
 */
public class APIGatewayProxyEventResourceControllerRequest extends BasicResourceControllerRequest {

    private final APIGatewayProxyRequestEvent event;

    protected APIGatewayProxyEventResourceControllerRequest(APIGatewayProxyRequestEvent event,
        HttpMethod method,
        ResourceData<?> body,
        String baseRequestUrl, String requestUrl, String contentType) {
        super(method, body,
            contentType, baseRequestUrl, requestUrl);
        this.event = event;
    }

    public static String getContentType(APIGatewayProxyRequestEvent event) {
        return event.getHeaders().get(ContentTypeNegotiation.CONTENT_TYPE);
    }

    public static Builder builder(APIGatewayProxyRequestEvent event) {
        return new Builder(event);
    }

    private static String[] toArray(List<String> list) {
        return isEmpty(list) ? new String[]{} : list.toArray(new String[0]);
    }

    @Override
    public Iterable<String> getHeaders(String name) {
        return event.getMultiValueHeaders().get(name);
    }

    @Override
    public List<String> getParameterNames() {
        return new ArrayList<>(event.getQueryStringParameters().keySet());
    }

    @Override
    public String[] getParameter(String parameterName) {
        List<String> list = event.getMultiValueQueryStringParameters().get(parameterName);
        return toArray(list);
    }

    public static class Builder extends
        AbstractResourceControllerRequestBuilder<Builder, APIGatewayProxyEventResourceControllerRequest> {

        private final APIGatewayProxyRequestEvent event;

        public Builder(APIGatewayProxyRequestEvent event) {
            this.event = event;
        }

        @Override
        protected Builder me() {
            return super.me();
        }

        @Override
        public APIGatewayProxyEventResourceControllerRequest build() {
            requestPath(event.getPath());
            method(HttpMethod.of(event.getHttpMethod()));
            String contentType = getContentType(
                () -> toArray(event.getMultiValueQueryStringParameters().get(contentTypeParam))
                , () -> APIGatewayProxyEventResourceControllerRequest.getContentType(event));
            return new APIGatewayProxyEventResourceControllerRequest(event, method, body,
                baseRequestUrl, baseRequestUrl + requestPath, contentType);
        }
    }
}
