package com.github.restup.spring.mvc.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import com.github.restup.controller.ResourceController;
import com.github.restup.http.model.HttpServletResourceControllerRequest;
import com.github.restup.http.model.HttpServletResourceControllerResponse;
import com.github.restup.jackson.service.model.JacksonRequestBody;
import com.github.restup.registry.ResourceRegistry;

/**
 * Implementation or example implementation for using Up! with Spring MVC
 */
public class SpringMVCResourceController {

    private final ResourceRegistry registry;
    private final ResourceController controller;
    private final String requestMapping;

    public SpringMVCResourceController(ResourceRegistry registry, ResourceController controller) {
        this("/**", registry, controller);
    }

    public SpringMVCResourceController(String requestMapping, ResourceRegistry registry, ResourceController controller) {
        this.requestMapping = cleanRequestMapping(requestMapping.trim());
        this.registry = registry;
        this.controller = controller;
    }

    public static HandlerMapping getHandlerMapping(SpringMVCResourceController controller, boolean async) throws NoSuchMethodException, SecurityException {
        Map<String, Object> urlMap = new HashMap<>();
        String method = async ? "asyncRequest" : "request";
        urlMap.put(controller.getRequestMapping(),
                new HandlerMethod(controller, SpringMVCResourceController.class.getMethod(method, HttpServletRequest.class, HttpServletResponse.class, JacksonRequestBody.class)));

        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setUrlMap(urlMap);
        simpleUrlHandlerMapping.setOrder(0);
        return simpleUrlHandlerMapping;
    }

    @ResponseBody
    public Object request(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) JacksonRequestBody body) {
        response.setStatus(200);
        return controller.request(HttpServletResourceControllerRequest.builder(request)
                .setBody(body)
                .setRegistry(registry), new HttpServletResourceControllerResponse(response));

    }

    @ResponseBody
    public Callable<Object> asyncRequest(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) JacksonRequestBody body) {
        return new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return request(request, response, body);
            }
        };
    }

    static String cleanRequestMapping(String requestMapping) {
        if (requestMapping.endsWith("/**")) {
            return requestMapping;
        } else if (requestMapping.endsWith("/")) {
            return requestMapping + "**";
        } else {
            return requestMapping + "/**";
        }
    }

    public String getRequestMapping() {
        return requestMapping;
    }

}
