package com.github.restup.controller.linking;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.service.model.response.PagedResult;

@RunWith(MockitoJUnitRunner.class)
public class DefaultLinkBuilderTest {

    @Mock
    ServiceDiscovery serviceDiscovery;
    @Mock
    ParsedResourceControllerRequest<?> request;

    final String requestUrl = "http://www.foo.com/bars/1";

    DefaultLinkBuilder linkBuilder;

    @Before
    public void before() {
        linkBuilder = new DefaultLinkBuilder(serviceDiscovery);
    }

    private void verifyTopLevelLinks() {
        verify(request).getRequestUrl();
        verify(request).getAcceptedParameterNames();
        verify(request).getRelationship();
        verifyNoMoreInteractions(serviceDiscovery, request);
    }

    private void setupTopLevelLinks() {
        when(request.getRequestUrl()).thenReturn(requestUrl);
    }

    private void assertLink(List<Link> links, int i, String name, String href) {
        assertEquals(name, links.get(i).getName());
        assertEquals(href, links.get(i).getHref());
    }

    @Test
    public void testList() {
        setupTopLevelLinks();

        List<Link> links = linkBuilder.getTopLevelLinks(request, new ArrayList<>());

        assertEquals(1, links.size());
        assertLink(links, 0, "self", requestUrl);

        verifyTopLevelLinks();
    }

    private PagedResult<?> paged(Integer limit, Integer offset, Long total) {
        PagedResult<?> result = mock(PagedResult.class);

        when(result.getLimit()).thenReturn(limit);
        when(result.getOffset()).thenReturn(offset);
        when(result.getTotal()).thenReturn(total);
        return result;
    }

    @Test
    public void testWithoutTotals() {
        PagedResult<?> result = paged(10, 10, null);

        setupTopLevelLinks();

        List<Link> links = linkBuilder.getTopLevelLinks(request, result);

        assertEquals(4, links.size());
        assertLink(links, 0, "first", requestUrl + "?limit=10&offset=0");
        assertLink(links, 1, "prev", requestUrl + "?limit=10&offset=9");
        assertLink(links, 2, "self", requestUrl + "?limit=10&offset=10");
        assertLink(links, 3, "next", requestUrl + "?limit=10&offset=11");

    }

    @Test
    public void testWithoutOffset() {
        PagedResult<?> result = paged(10, null, null);

        setupTopLevelLinks();

        List<Link> links = linkBuilder.getTopLevelLinks(request, result);

        assertEquals(1, links.size());
        assertLink(links, 0, "self", requestUrl);

        verifyTopLevelLinks();
    }

}
