package com.github.restup.spring.mvc.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.github.restup.controller.ResourceController;
import com.github.restup.http.model.HttpServletResourceControllerRequest;
import com.github.restup.http.model.HttpServletResourceControllerResponse;
import com.github.restup.jackson.service.model.JacksonRequestBody;
import com.github.restup.registry.ResourceRegistry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SpringMVCResourceControllerTest {
    
    @Mock
    ResourceRegistry registry;
    @Mock
    ResourceController controller;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    JacksonRequestBody body;
    
    @Test
    public void testRequest() {
        SpringMVCResourceController mvc = new UpSpringMVCConfiguration().upSpringMVCResourceController(registry, controller);
        mvc.request(request, response, body);

        ArgumentCaptor<HttpServletResourceControllerRequest.Builder> builder = ArgumentCaptor.forClass(HttpServletResourceControllerRequest.Builder.class);
        ArgumentCaptor<HttpServletResourceControllerResponse> controllerResponse = ArgumentCaptor.forClass(HttpServletResourceControllerResponse.class);
        verify(controller).request(builder.capture(), controllerResponse.capture());
        verifyNoMoreInteractions(registry, controller, request, response, body);
    }

}
