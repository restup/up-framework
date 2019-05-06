package com.github.restup.controller.settings;

import static com.github.restup.util.UpUtils.nvl;

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

    public void setLinkBuilderFactory(LinkBuilderFactory linkBuilderFactory) {
        if (linkBuilderFactory != null) {
            this.linkBuilderFactory = linkBuilderFactory;
        }
    }

    public ServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }

    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        if (serviceDiscovery != null) {
            this.serviceDiscovery = serviceDiscovery;
        }
    }

    public Boolean getAutoDetectDisabled() {
        return autoDetectDisabled;
    }

    public void setAutoDetectDisabled(Boolean autoDetectDisabled) {
        if (autoDetectDisabled != null) {
            this.autoDetectDisabled = autoDetectDisabled;
        }
    }

    public String getDefaultMediaType() {
        return defaultMediaType;
    }

    public void setDefaultMediaType(String defaultMediaType) {
        if (defaultMediaType != null) {
            this.defaultMediaType = defaultMediaType;
        }
    }

    public BuilderSettingsCaptor capture(BuilderSettingsCaptor settingsCaptor) {
        setLinkBuilderFactory(nvl(settingsCaptor.getLinkBuilderFactory(), getLinkBuilderFactory()));
        setAutoDetectDisabled(nvl(settingsCaptor.getAutoDetectDisabled(), getAutoDetectDisabled()));
        setDefaultMediaType(nvl(settingsCaptor.getDefaultMediaType(), getDefaultMediaType()));
        setServiceDiscovery(nvl(settingsCaptor.getServiceDiscovery(), getServiceDiscovery()));
        return this;
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
