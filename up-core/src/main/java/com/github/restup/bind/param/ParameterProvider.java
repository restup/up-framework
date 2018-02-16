package com.github.restup.bind.param;

import java.util.List;

/**
 * Interface for accessing request parameters. <p> Intended to typically wrap an HttpServletRequest, however by using ParameterProvider instead, core Up! is decoupled from servlet apis. <p> Access to request parameters directly is required when binding values to pojos used as filter method arguments
 */
public interface ParameterProvider {

    /**
     * @return A list of all parameter names
     */
    List<String> getParameterNames();

    /**
     * @param parameterName name of parameter
     * @return all values for the specified parameterName
     */
    String[] getParameter(String parameterName);

}
