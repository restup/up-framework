package com.github.restup.controller.request.parser;

import com.github.restup.controller.model.ParsedResourceControllerRequest;
import com.github.restup.controller.model.ResourceControllerRequest;

/**
 * Defines a parser which handles a request parameter
 *
 * @author abuttaro
 */
public interface RequestParamParser {

    /**
     * @param parameterName name of the parameter, never null
     * @return true if the parser will handle the parameter, false otherwise
     */
    boolean accept(String parameterName);

    /**
     * Parses the parameter as needed and appends appropriate details to the
     * {@link ParsedResourceControllerRequest.Builder}
     * 
     * @param request
     * @param builder
     * @param <T>
     *
     * @param parameterName name of the parameter, never null
     * @param parameterValue
     */
    <T> void parse(ResourceControllerRequest request, ParsedResourceControllerRequest.Builder<T> builder, String parameterName, String[] parameterValue);

}
