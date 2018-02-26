package com.github.restup.registry.settings;

/**
 * Settings which define which methods are exposed internally by service implementations. Disabling these service will disallow executing them internally as well as via http endpoints. However, if enabled, they may still be disabled by {@link ControllerMethodAccess}. By default all are enabled.
 */
class BasicMethodAccess implements MethodAccess {

    private final boolean createDisabled;
    private final boolean createMultipleDisabled;
    private final boolean getByIdDisabled;
    private final boolean listDisabled;
    private final boolean deleteByIdDisabled;
    private final boolean deleteByIdsDisabled;
    private final boolean patchByIdDisabled;
    private final boolean patchMultipleDisabled;
    private final boolean deleteByQueryDisabled;
    private final boolean patchByQueryDisabled;

    BasicMethodAccess(boolean createDisabled, boolean createMultipleDisabled, boolean getByIdDisabled, boolean listDisabled, boolean deleteByIdDisabled, boolean deleteByIdsDisabled, boolean patchByIdDisabled, boolean patchMultipleDisabled, boolean deleteByQueryDisabled, boolean patchByQueryDisabled) {
        this.createDisabled = createDisabled;
        this.createMultipleDisabled = createMultipleDisabled;
        this.getByIdDisabled = getByIdDisabled;
        this.listDisabled = listDisabled;
        this.deleteByIdDisabled = deleteByIdDisabled;
        this.deleteByIdsDisabled = deleteByIdsDisabled;
        this.patchByIdDisabled = patchByIdDisabled;
        this.patchMultipleDisabled = patchMultipleDisabled;
        this.deleteByQueryDisabled = deleteByQueryDisabled;
        this.patchByQueryDisabled = patchByQueryDisabled;
    }

    @Override
    public boolean isCreateDisabled() {
        return createDisabled;
    }

    @Override
    public boolean isCreateMultipleDisabled() {
        return createMultipleDisabled;
    }

    @Override
    public boolean isGetByIdDisabled() {
        return getByIdDisabled;
    }

    @Override
    public boolean isListDisabled() {
        return listDisabled;
    }

    @Override
    public boolean isDeleteByIdDisabled() {
        return deleteByIdDisabled;
    }

    @Override
    public boolean isDeleteByIdsDisabled() {
        return deleteByIdsDisabled;
    }

    @Override
    public boolean isPatchByIdDisabled() {
        return patchByIdDisabled;
    }

    @Override
    public boolean isPatchMultipleDisabled() {
        return patchMultipleDisabled;
    }

    @Override
    public boolean isDeleteByQueryDisabled() {
        return deleteByQueryDisabled;
    }

    @Override
    public boolean isPatchByQueryDisabled() {
        return patchByQueryDisabled;
    }

}