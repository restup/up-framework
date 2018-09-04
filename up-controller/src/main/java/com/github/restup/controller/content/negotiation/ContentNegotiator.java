package com.github.restup.controller.content.negotiation;

import com.github.restup.controller.linking.LinkBuilderFactory;
import com.github.restup.controller.linking.discovery.ServiceDiscovery;
import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerResponse;
import com.github.restup.controller.settings.BuilderSettingsCaptor;
import com.github.restup.registry.settings.AutoDetectConstants;
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

    static Builder builder() {
        return new Builder();
    }

    class Builder {

        private ContentNegotiator[] contentNegotiators;
        private BuilderSettingsCaptor settingsCaptor;

        Builder() {
            super();
            settingsCaptor = new BuilderSettingsCaptor();
        }

        Builder me() {
            return this;
        }

        public Builder contentNegotiators(ContentNegotiator... contentNegotiators) {
            this.contentNegotiators = contentNegotiators;
            return this.me();
        }

        public Builder autoDetectDisabled(boolean autoDetectDisabled) {
            this.settingsCaptor.setAutoDetectDisabled(autoDetectDisabled);
            return this.me();
        }

        public Builder defaultMediaType(String mediaType) {
            this.settingsCaptor.setDefaultMediaType(mediaType);
            return this.me();
        }

        public Builder serviceDiscovery(ServiceDiscovery serviceDiscovery) {
            this.settingsCaptor.setServiceDiscovery(serviceDiscovery);
            return this.me();
        }

        public Builder linkBuilderFactory(LinkBuilderFactory linkBuilderFactory) {
            this.settingsCaptor.setLinkBuilderFactory(linkBuilderFactory);
            return this.me();
        }

        public Builder capture(BuilderSettingsCaptor settingsCaptor) {
            this.settingsCaptor = settingsCaptor.capture(this.settingsCaptor);
            return this.me();
        }

        public ContentNegotiator build() {
            this.settingsCaptor.build();

            ContentNegotiator[] arr = this.contentNegotiators;
            if (!this.settingsCaptor.getAutoDetectDisabled()) {
                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    arr = ArrayUtils
                        .addAll(arr, new JsonContentNegotiator(), new JsonApiContentNegotiator(
                            this.settingsCaptor.getLinkBuilderFactory()));
                }
            }
            if (this.settingsCaptor.getDefaultMediaType() != null) {
                DefaultContentNegotiator defaultContentNegotiator = new DefaultContentNegotiator(
                    this.settingsCaptor.getDefaultMediaType(), arr);
                arr = ArrayUtils.add(arr, defaultContentNegotiator);
            }
            return arr != null ? new ContentNegotiatorChain(arr) : new ContentNegotiatorChain();
        }

    }

}
