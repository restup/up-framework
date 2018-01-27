package com.github.restup.errors;

/**
 * Default {@link ErrorFactory} implementation
 */
final class DefaultErrorFactory implements ErrorFactory {

    private static volatile DefaultErrorFactory instance = null;

    static DefaultErrorFactory getInstance() {
        if (instance == null) {
            synchronized (DefaultErrorFactory.class) {
                if (instance == null) {
                    instance = new DefaultErrorFactory();
                }
            }
        }
        return instance;
    }

    private DefaultErrorFactory() {

    }

    @Override
    public Errors createErrors() {
        return new BasicErrors();
    }

    @Override
    public ParameterError createParameterError(String parameterName, Object parameterValue) {
        return new BasicParameterError(parameterName, parameterValue);
    }

}
