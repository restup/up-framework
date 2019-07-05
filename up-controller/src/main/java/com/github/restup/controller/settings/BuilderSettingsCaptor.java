package com.github.restup.controller.settings;

import static com.github.restup.util.UpUtils.nvl;

import com.github.restup.config.ConfigurationContext;
import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.CachedServiceDiscovery;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.util.Assert;

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
    private ConfigurationContext configurationContext;

    public LinkBuilderFactory getLinkBuilderFactory() {
        return linkBuilderFactory;
    }

    public void setLinkBuilderFactory(LinkBuilderFactory linkBuilderFactory) {
        this.linkBuilderFactory = nvl(linkBuilderFactory, this.linkBuilderFactory);
    }

    public ServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }

    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = nvl(serviceDiscovery, this.serviceDiscovery);
    }

    public Boolean getAutoDetectDisabled() {
        return autoDetectDisabled;
    }

    public void setAutoDetectDisabled(Boolean autoDetectDisabled) {
        this.autoDetectDisabled = nvl(autoDetectDisabled, this.autoDetectDisabled);
    }

    public String getDefaultMediaType() {
        return defaultMediaType;
    }

    public void setDefaultMediaType(String defaultMediaType) {
        this.defaultMediaType = nvl(defaultMediaType, this.defaultMediaType);
    }

    public ConfigurationContext getConfigurationContext() {
        return configurationContext;
    }

    public void setConfigurationContext(ConfigurationContext configurationContext) {
        this.configurationContext = nvl(configurationContext, this.configurationContext);
    }

    public BuilderSettingsCaptor capture(BuilderSettingsCaptor settingsCaptor) {
        setLinkBuilderFactory(nvl(settingsCaptor.getLinkBuilderFactory(), getLinkBuilderFactory()));
        setAutoDetectDisabled(nvl(settingsCaptor.getAutoDetectDisabled(), getAutoDetectDisabled()));
        setDefaultMediaType(nvl(settingsCaptor.getDefaultMediaType(), getDefaultMediaType()));
        setServiceDiscovery(nvl(settingsCaptor.getServiceDiscovery(), getServiceDiscovery()));
        setConfigurationContext(
            nvl(settingsCaptor.getConfigurationContext(), getConfigurationContext()));
        return this;
    }

    public void build() {
        Assert.notNull(configurationContext, "configurationContext is required");

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

        setDefaultMediaType(
            configurationContext
                .getProperty(ConfigurationContext.DEFAULT_MEDIA_TYPE, defaultMediaType));

        setAutoDetectDisabled(
            configurationContext
                .getProperty(ConfigurationContext.AUTO_DETECT_DISABLED, autoDetectDisabled));

    }

}
