package com.github.restup.registry.settings;

/**
 * Configures Contoller method access.  Disabling methods will ensure methods are not exposed over http, however they still may be available internally depending up on {@link ServiceMethodAccess} settings. Similarly, enabled methods may still be disabled if the service method setting is disabled. By default, all are enabled.
 */
public final class ControllerMethodAccess extends ServiceMethodAccess {

    private final boolean getByIdsDisabled;
    private final boolean patchByIdsDisabled;
    private final boolean updateByIdDisabled;
    private final boolean updateMultipleDisabled;

    public ControllerMethodAccess(boolean createDisabled, boolean createMultipleDisabled, boolean getByIdDisabled, boolean listDisabled, boolean deleteByIdDisabled, boolean deleteByIdsDisabled, boolean patchByIdDisabled, boolean patchMultipleDisabled, boolean deleteByQueryDisabled, boolean patchByQueryDisabled, boolean getByIdsDisabled, boolean patchByIdsDisabled, boolean updateByIdDisabled, boolean updateMultipleDisabled) {
        super(createDisabled, createMultipleDisabled, getByIdDisabled, listDisabled, deleteByIdDisabled, deleteByIdsDisabled, patchByIdDisabled, patchMultipleDisabled, deleteByQueryDisabled, patchByQueryDisabled);
        this.getByIdsDisabled = getByIdsDisabled;
        this.patchByIdsDisabled = patchByIdsDisabled;
        this.updateByIdDisabled = updateByIdDisabled;
        this.updateMultipleDisabled = updateMultipleDisabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isGetByIdsDisabled() {
        return getByIdsDisabled;
    }

    public boolean isPatchByIdsDisabled() {
        return patchByIdsDisabled;
    }

    public boolean isUpdateByIdDisabled() {
        return updateByIdDisabled;
    }

    public boolean isUpdateMultipleDisabled() {
        return updateMultipleDisabled;
    }

    public static class Builder extends ServiceMethodAccess.AbstractBuilder<Builder, ControllerMethodAccess> {

        private boolean getByIdsDisabled;
        private boolean patchByIdsDisabled;
        private boolean updateByIdDisabled;
        private boolean updateMultipleDisabled;

        public Builder setAllDisabled(boolean b) {
            return super.setAllDisabled(b)
                    .setGetByIdsDisabled(b)
                    .setPatchByIdsDisabled(b)
                    .setUpdateByIdDisabled(b)
                    .setUpdateMultipleDisabled(b);
        }

        public Builder setGetByIdsDisabled(boolean getByIdsDisabled) {
            this.getByIdsDisabled = getByIdsDisabled;
            return me();
        }

        public Builder setPatchByIdsDisabled(boolean patchByIdsDisabled) {
            this.patchByIdsDisabled = patchByIdsDisabled;
            return me();
        }

        public Builder setUpdateByIdDisabled(boolean updateByIdDisabled) {
            this.updateByIdDisabled = updateByIdDisabled;
            return me();
        }

        public Builder setUpdateMultipleDisabled(boolean updateMultipleDisabled) {
            this.updateMultipleDisabled = updateMultipleDisabled;
            return me();
        }

        public ControllerMethodAccess build() {
            return new ControllerMethodAccess(createDisabled, createMultipleDisabled, getByIdDisabled, listDisabled, deleteByIdDisabled, deleteByIdsDisabled, patchByIdDisabled, patchMultipleDisabled, deleteByQueryDisabled, patchByQueryDisabled, getByIdsDisabled, patchByIdsDisabled, updateByIdDisabled, updateMultipleDisabled);
        }
    }

}