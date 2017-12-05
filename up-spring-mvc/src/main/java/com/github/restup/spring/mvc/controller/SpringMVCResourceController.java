package com.github.restup.spring.mvc.controller;

import com.github.restup.controller.ResourceController;
import com.github.restup.http.model.HttpServletResourceControllerRequest;
import com.github.restup.http.model.HttpServletResourceControllerResponse;
import com.github.restup.jackson.service.model.JacksonRequestBody;
import com.github.restup.registry.ResourceRegistry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementation or example implementation for using Up! with Spring MVC
 */
@RestController
public class SpringMVCResourceController {

    private final ResourceRegistry registry;
    private final ResourceController controller;

    public SpringMVCResourceController(ResourceRegistry registry, ResourceController controller) {
        this.registry = registry;
        this.controller = controller;
    }

    @RequestMapping("/**")
    public Object path(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = false) JacksonRequestBody body) {
        response.setStatus(200);
        return controller.request(HttpServletResourceControllerRequest.builder(request)
                        .setBody(body)
                        .setRegistry(registry)
                , new HttpServletResourceControllerResponse(response));
    }

}
