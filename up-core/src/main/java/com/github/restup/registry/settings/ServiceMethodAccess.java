package com.github.restup.registry.settings;

/**
 * Settings which define which methods are exposed internally by service implementations. Disabling
 * these service will disallow executing them internally as well as via http endpoints. However, if
 * enabled, they may still be disabled by {@link ControllerMethodAccess}. By default all are
 * enabled.
 */
public interface ServiceMethodAccess extends MethodAccess {

    static Builder builder() {
        return new Builder();
    }

    static ServiceMethodAccess allEnabled() {
        return builder().setAllEnabled().build();
    }

    class Builder extends AbstractMethodAccessBuilder<Builder, ServiceMethodAccess> {

        private Builder() {
            super();
        }

        @Override
        public ServiceMethodAccess build() {
            return new BasicServiceMethodAccess(createDisabled, createMultipleDisabled, getByIdDisabled, listDisabled, deleteByIdDisabled, deleteByIdsDisabled, patchByIdDisabled,
                    patchMultipleDisabled, deleteByQueryDisabled, patchByQueryDisabled);

        }
    }

}
