package com.github.restup.errors;

/**
 * A factory to obtain error object instances.
 *
 * @author andy.buttaro
 */
public abstract class ErrorFactory {

    private static volatile ErrorFactory instance = null;

    public static ErrorFactory getInstance() {
        if (instance == null) {
            synchronized (ErrorFactory.class) {
                if (instance == null) {
                    instance = getDefaultErrorFactory();
                }
            }
        }
        return instance;
    }

    /**
     * Register an instance to be used by default by {@link ErrorBuilder}
     */
    public synchronized static void registerErrorFactory(ErrorFactory factory) {
        instance = factory;
    }

    public static ErrorFactory getDefaultErrorFactory() {
        return new DefaultErrorFactory();
    }

    /**
     * @return A new {@link Errors} instance
     */
    public abstract Errors createErrors();

    /**
     * @return A new {@link ParameterError} instance
     */
    public abstract ParameterError createParameterError(String parameterName, Object parameterValue);

}
