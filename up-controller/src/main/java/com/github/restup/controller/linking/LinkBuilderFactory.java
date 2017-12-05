package com.github.restup.controller.linking;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.settings.ControllerSettings;

/**
 * A factory for providing {@link LinkBuilder} implementations which may be configured using {@link ControllerSettings.Builder#linkBuilderFactory(LinkBuilderFactory)}
 */
public interface LinkBuilderFactory {

    /**
     * The resulting {@link LinkBuilder} could be implemented as singleton, thread safe or accept the arguments for specific requests.
     *
     * @return a link builder for the request.
     */
    LinkBuilder getLinkBuilder(ParsedResourceControllerRequest<?> request, Object result);

}
