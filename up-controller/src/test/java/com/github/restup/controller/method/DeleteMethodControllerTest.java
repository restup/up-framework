package com.github.restup.controller.method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.restup.bind.param.ParameterProvider;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.registry.Resource;
import com.github.restup.service.model.request.DefaultRequestObjectFactory;
import org.junit.Test;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DeleteMethodControllerTest {

    @Test
    public void testGetRequest() {
        ParsedResourceControllerRequest details = mock(ParsedResourceControllerRequest.class);
        when(details.getParameter("foo")).thenReturn(new String[]{"bar"});
        when(details.getParameter("fu")).thenReturn(null);
        DeleteMethodController m = new DeleteMethodController(new DefaultRequestObjectFactory());
        ParameterProvider request = (ParameterProvider) m.getRequest(mock(Resource.class), null, null, details);
        assertThat(request.getParameter("foo"), is(new String[]{"bar"}));
        assertNull(request.getParameter("fu"));
    }

}
