package com.github.restup.controller.settings;

import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.CachedServiceDiscovery;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;

/**
 * Captures state shared across {@link ControllerSettings.Builder}},
 * {@link com.github.restup.controller.content.negotiation.ContentNegotiator.Builder} and
 * {@link com.github.restup.controller.request.parser.RequestParser.Builder}
 * 
 * 
 * @author abuttaro
 *
 */
public class BuilderSettingsCaptor {

    private LinkBuilderFactory linkBuilderFactory;
    private ServiceDiscovery serviceDiscovery;
    private Boolean autoDetectDisabled;
    private String defaultMediaType;

    public LinkBuilderFactory getLinkBuilderFactory() {
        return linkBuilderFactory;
    }

    public ServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }

    public Boolean getAutoDetectDisabled() {
        return autoDetectDisabled;
    }

    public String getDefaultMediaType() {
        return defaultMediaType;
    }

    public BuilderSettingsCaptor capture(BuilderSettingsCaptor settingsCaptor) {
        setLinkBuilderFactory(settingsCaptor.getLinkBuilderFactory());
        setAutoDetectDisabled(settingsCaptor.getAutoDetectDisabled());
        setDefaultMediaType(settingsCaptor.getDefaultMediaType());
        setServiceDiscovery(settingsCaptor.getServiceDiscovery());
        return this;
    }

    public void setLinkBuilderFactory(LinkBuilderFactory linkBuilderFactory) {
        if (linkBuilderFactory != null) {
            this.linkBuilderFactory = linkBuilderFactory;
        }
    }

    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        if (serviceDiscovery != null) {
            this.serviceDiscovery = serviceDiscovery;
        }
    }

    public void setAutoDetectDisabled(Boolean autoDetectDisabled) {
        if (autoDetectDisabled != null) {
            this.autoDetectDisabled = autoDetectDisabled;
        }
    }

    public void setDefaultMediaType(String defaultMediaType) {
        if (defaultMediaType != null) {
            this.defaultMediaType = defaultMediaType;
        }
    }

    public void build() {
        ServiceDiscovery discovery = serviceDiscovery;
        LinkBuilderFactory factory = linkBuilderFactory;
        if (discovery == null) {
            discovery = CachedServiceDiscovery
                    .cache(ServiceDiscovery.getDefaultServiceDiscovery());
            serviceDiscovery = discovery;
        }
        if (factory == null) {
            factory = LinkBuilderFactory.getDefaultLinkBuilderFactory(discovery);
            linkBuilderFactory = factory;
        }
        if (autoDetectDisabled == null) {
            autoDetectDisabled = false;
        }
    }

}
