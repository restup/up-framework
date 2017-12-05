package com.github.restup.test.spring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import com.github.restup.test.ApiExecutor;
import com.github.restup.test.ApiRequest;
import com.github.restup.test.ApiResponse;
import com.github.restup.test.RpcApiTest;
import com.github.restup.test.resource.Contents;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * mockMvc {@link ApiExecutor}
 */
@Component
public class MockMVCApiExecutor implements ApiExecutor {

    protected static final Logger log = LoggerFactory.getLogger(MockMVCApiExecutor.class);

    private final MockMvc mockMvc;

    @Autowired
    public MockMVCApiExecutor(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    public static ResultActions debug(ResultActions result) throws Exception {
        MvcResult mvcResult = result.andReturn();
        MockHttpServletRequest request = mvcResult.getRequest();
        // MockHttpServletResponse response = mvcResult.getResponse();
        if (log.isDebugEnabled()) {
            log.debug("Response from {}", request.getRequestURL());// , response(response)
            try {
                result.andDo(MockMvcResultHandlers.print());
            } catch (NoSuchMethodError e) {
                log.warn("", e);
            }
        }
        return result;
    }

    /**
     * @return a {@link ResultActionsApiResponse} with mockMvc results
     */
    public ApiResponse<String[]> execute(RpcApiTest rpcApiTest) {
        ApiRequest test = rpcApiTest.getRequest();
        assertThat(test.getMethod(), notNullValue());

        String requestUrl = test.getUrl();
        assertThat(requestUrl, notNullValue());

        HttpMethod httpMethod = HttpMethod.valueOf(test.getMethod().name());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.request(httpMethod, requestUrl);

        Contents contents = test.getBody();
        if (contents != null) {
            request.content(contents.getContentAsByteArray());
        }

        if (test.getHeaders() != null) {
            for (Entry<String, String[]> e : test.getHeaders().entrySet()) {
                for (String value : e.getValue()) {
                    request.header(e.getKey(), value);
                }
            }
        }

        request.secure(test.isHttps());

        try {

            ResultActions ra = mockMvc.perform(request);

            debug(ra);

            MvcResult result = ra.andReturn();
            MockHttpServletResponse response = result.getResponse();

            Map<String, String[]> headers = new HashMap<String, String[]>();
            for (String header : response.getHeaderNames()) {
                headers.put(header, response.getHeaders(header).toArray(new String[0]));
            }
            return new ResultActionsApiResponse(ra, response.getStatus(), headers, response.getContentAsByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static class ResultActionsApiResponse extends ApiResponse<String[]> {

        private final ResultActions resultActions;

        public ResultActionsApiResponse(ResultActions resultActions, int status, Map<String, String[]> headers, byte[] body) {
            super(status, headers, body);
            this.resultActions = resultActions;
        }

        public ResultActions getResultActions() {
            return resultActions;
        }

    }
}
