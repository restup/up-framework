package com.github.restup.bind.param;

import java.util.Collections;
import java.util.List;

/**
 * Lazily initialized singleton {@link ParameterProvider} with no op implementations.
 */
public class NoOpParameterProvider implements ParameterProvider {

    private static volatile NoOpParameterProvider instance = null;

    private NoOpParameterProvider() {
        super();
    }

    public static NoOpParameterProvider getInstance() {
        if (instance == null) {
            synchronized (NoOpParameterProvider.class) {
                if (instance == null) {
                    instance = new NoOpParameterProvider();
                }
            }
        }
        return instance;
    }

    /**
     * @return empty, immutable list always, never null
     */
    public List<String> getParameterNames() {
        return Collections.emptyList();
    }

    /**
     * @return null always
     */
    public String[] getParameter(String parameterName) {
        return null;
    }
}
