package com.github.restup.registry.settings;

/**
 * Settings which define which methods are exposed internally by service implementations. Disabling these service will disallow executing them internally as well as via http endpoints. However, if enabled, they may still be disabled by {@link ControllerMethodAccess}. By default all are enabled.
 */
class BasicServiceMethodAccess extends BasicMethodAccess implements ServiceMethodAccess {

    BasicServiceMethodAccess(boolean createDisabled, boolean createMultipleDisabled, boolean getByIdDisabled, boolean listDisabled, boolean deleteByIdDisabled,
            boolean deleteByIdsDisabled, boolean patchByIdDisabled, boolean patchMultipleDisabled, boolean deleteByQueryDisabled, boolean patchByQueryDisabled) {
        super(createDisabled, createMultipleDisabled, getByIdDisabled, listDisabled, deleteByIdDisabled, deleteByIdsDisabled, patchByIdDisabled, patchMultipleDisabled,
                deleteByQueryDisabled, patchByQueryDisabled);
    }

}