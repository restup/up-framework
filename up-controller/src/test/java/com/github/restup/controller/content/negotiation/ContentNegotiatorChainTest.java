package com.github.restup.controller.content.negotiation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.restup.config.ConfigurationContext;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.model.ResourceControllerRequest;
import org.junit.Test;

public class ContentNegotiatorChainTest {

    @Test
    public void testEmpty() {
        ContentNegotiatorChain chain = new ContentNegotiatorChain();
        assertFalse(chain.accept(null));
    }

    @Test
    public void testDefaults() {
        ResourceControllerRequest request = mock(ResourceControllerRequest.class);
        ContentNegotiatorChain chain = new ContentNegotiatorChain(
                ContentNegotiator.builder().autoDetectDisabled(true)
                    .configurationContext(mock(ConfigurationContext.class))
                        .serviceDiscovery(ServiceDiscovery.getDefaultServiceDiscovery())
                        .linkBuilderFactory(LinkBuilderFactory.getDefaultLinkBuilderFactory(ServiceDiscovery.getDefaultServiceDiscovery())).build());
        assertFalse(chain.accept(request));
    }

    @Test
    public void testAccepts() {
        ResourceControllerRequest request = mock(ResourceControllerRequest.class);
        ContentNegotiator negotiator = mock(ContentNegotiator.class);
        when(negotiator.accept(request)).thenReturn(true);
        ContentNegotiatorChain chain = new ContentNegotiatorChain(negotiator);
        assertTrue(chain.accept(request));
    }

    @Test
    public void testDefault() {
        ResourceControllerRequest request = mock(ResourceControllerRequest.class);
        ContentNegotiatorChain chain = new ContentNegotiatorChain(
                ContentNegotiator.builder()
                    .configurationContext(mock(ConfigurationContext.class))
                    .defaultMediaType("application/json").build());
        assertTrue(chain.accept(request));
    }
}
