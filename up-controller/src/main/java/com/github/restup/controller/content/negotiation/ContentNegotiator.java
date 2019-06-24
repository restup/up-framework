package com.github.restup.controller.content.negotiation;

import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.controller.settings.BuilderSettingsCaptor;
import com.github.restup.registry.settings.AutoDetectConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Negotiates return types based upon request details. <p> While this may query serialization using libraries such as Jackson or Gson, it is also likely that the object types and structure may have to change to modify the response structure depending upon the request context.
 *
 * @author abuttaro
 */
public interface ContentNegotiator {

    static Builder builder() {
        return new Builder();
    }

    /**
     * @param request to check if accepted
     * @return true if the controller will accept the request, false otherwise
     */
    boolean accept(ResourceControllerRequest request);

    /**
     * Reformats an object for response. This may be required to alter the response for a different
     * presentation (json, json api, hal, etc)
     *
     * @param <T> type of resource requested
     *
     * @param request producing the response
     * @param response for the request
     * @param result result object of the operation
     * @return a new, possibly reformatted object
     */
    <T> Object formatResponse(ParsedResourceControllerRequest<T> request, ResourceControllerResponse response, Object result);

    class Builder {

        private ContentNegotiator[] contentNegotiators;
        private BuilderSettingsCaptor settingsCaptor;
        private List<ContentNegotiatorBuilderDecorator> contentNegotiatorBuilderDecorators = new ArrayList<>();


        Builder() {
            super();
            settingsCaptor = new BuilderSettingsCaptor();
        }

        Builder me() {
            return this;
        }

        public Builder contentNegotiators(ContentNegotiator... contentNegotiators) {
            this.contentNegotiators = contentNegotiators;
            return me();
        }

        public Builder autoDetectDisabled(boolean autoDetectDisabled) {
            settingsCaptor.setAutoDetectDisabled(autoDetectDisabled);
            return me();
        }

        public Builder defaultMediaType(String mediaType) {
            settingsCaptor.setDefaultMediaType(mediaType);
            return me();
        }

        public Builder serviceDiscovery(ServiceDiscovery serviceDiscovery) {
            settingsCaptor.setServiceDiscovery(serviceDiscovery);
            return me();
        }

        public Builder linkBuilderFactory(LinkBuilderFactory linkBuilderFactory) {
            settingsCaptor.setLinkBuilderFactory(linkBuilderFactory);
            return me();
        }

        public Builder capture(BuilderSettingsCaptor settingsCaptor) {
            this.settingsCaptor = settingsCaptor.capture(this.settingsCaptor);
            return me();
        }

        public Builder decorate(ContentNegotiatorBuilderDecorator... decorators) {
            for (ContentNegotiatorBuilderDecorator decorator : decorators) {
                contentNegotiatorBuilderDecorators.add(decorator);
            }
            return me();
        }

        public Builder decorate(Collection<ContentNegotiatorBuilderDecorator> decorators) {
            contentNegotiatorBuilderDecorators.addAll(decorators);
            return me();
        }

        public ContentNegotiator build() {
            contentNegotiatorBuilderDecorators.stream().forEach(d -> d.decorate(this));
            settingsCaptor.build();

            ContentNegotiator[] arr = contentNegotiators;
            if (!settingsCaptor.getAutoDetectDisabled()) {
                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    arr = ArrayUtils
                        .addAll(arr, new JsonContentNegotiator(), new JsonApiContentNegotiator(
                            settingsCaptor.getLinkBuilderFactory()));
                }
            }
            if (settingsCaptor.getDefaultMediaType() != null) {
                DefaultContentNegotiator defaultContentNegotiator = new DefaultContentNegotiator(
                    settingsCaptor.getDefaultMediaType(), arr);
                arr = ArrayUtils.add(arr, defaultContentNegotiator);
            }
            return arr != null ? new ContentNegotiatorChain(arr) : new ContentNegotiatorChain();
        }
    }

}
