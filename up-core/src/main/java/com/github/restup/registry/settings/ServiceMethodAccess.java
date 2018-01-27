package com.github.restup.registry.settings;

/**
 * Settings which define which methods are exposed internally by service implementations. Disabling these service will disallow executing them internally as well as via http endpoints. However, if enabled, they may still be disabled by {@link ControllerMethodAccess}. By default all are enabled.
 */
public interface ServiceMethodAccess {


    boolean isCreateDisabled();

    boolean isCreateMultipleDisabled();

    boolean isGetByIdDisabled();

    boolean isListDisabled();

    boolean isDeleteByIdDisabled();

    boolean isDeleteByIdsDisabled();

    boolean isPatchByIdDisabled();

    boolean isPatchMultipleDisabled();

    boolean isDeleteByQueryDisabled();

    boolean isPatchByQueryDisabled();
    
    static Builder builder() {
        return new Builder();
    }

    static class Builder extends AbstractMethodAccessBuilder<Builder, ServiceMethodAccess> {

        private Builder() {
            super();
        }
    }

}