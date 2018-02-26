package com.github.restup.registry.settings;

/**
 * Configures Contoller method access.  Disabling methods will ensure methods are not exposed over http, however they still may be available internally depending up on {@link ServiceMethodAccess} settings. Similarly, enabled methods may still be disabled if the service method setting is disabled. By default, all are enabled.
 */
final class BasicControllerMethodAccess extends BasicMethodAccess implements ControllerMethodAccess {

    private final boolean getByIdsDisabled;
    private final boolean patchByIdsDisabled;
    private final boolean updateByIdDisabled;
    private final boolean updateMultipleDisabled;

    BasicControllerMethodAccess(boolean createDisabled, boolean createMultipleDisabled, boolean getByIdDisabled, boolean listDisabled, boolean deleteByIdDisabled, boolean deleteByIdsDisabled, boolean patchByIdDisabled, boolean patchMultipleDisabled, boolean deleteByQueryDisabled, boolean patchByQueryDisabled, boolean getByIdsDisabled, boolean patchByIdsDisabled, boolean updateByIdDisabled, boolean updateMultipleDisabled) {
        super(createDisabled, createMultipleDisabled, getByIdDisabled, listDisabled, deleteByIdDisabled, deleteByIdsDisabled, patchByIdDisabled, patchMultipleDisabled, deleteByQueryDisabled, patchByQueryDisabled);
        this.getByIdsDisabled = getByIdsDisabled;
        this.patchByIdsDisabled = patchByIdsDisabled;
        this.updateByIdDisabled = updateByIdDisabled;
        this.updateMultipleDisabled = updateMultipleDisabled;
    }

    @Override
    public boolean isGetByIdsDisabled() {
        return getByIdsDisabled;
    }

    @Override
    public boolean isPatchByIdsDisabled() {
        return patchByIdsDisabled;
    }

    @Override
    public boolean isUpdateByIdDisabled() {
        return updateByIdDisabled;
    }

    @Override
    public boolean isUpdateMultipleDisabled() {
        return updateMultipleDisabled;
    }

}