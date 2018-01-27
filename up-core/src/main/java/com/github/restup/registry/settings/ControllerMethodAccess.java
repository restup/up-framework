package com.github.restup.registry.settings;

/**
 * Configures Contoller method access. Disabling methods will ensure methods are not exposed over
 * http, however they still may be available internally depending up on {@link ServiceMethodAccess}
 * settings. Similarly, enabled methods may still be disabled if the service method setting is
 * disabled. By default, all are enabled.
 */
public interface ControllerMethodAccess extends ServiceMethodAccess {


    static Builder builder() {
        return new Builder();
    }

    boolean isGetByIdsDisabled();

    boolean isPatchByIdsDisabled();

    boolean isUpdateByIdDisabled();

    boolean isUpdateMultipleDisabled();

    static class Builder extends AbstractMethodAccessBuilder<Builder, ControllerMethodAccess> {

        private boolean getByIdsDisabled;
        private boolean patchByIdsDisabled;
        private boolean updateByIdDisabled;
        private boolean updateMultipleDisabled;
        
        private Builder() {
            
        }

        @Override
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

        @Override
        public ControllerMethodAccess build() {
            return new BasicControllerMethodAccess(createDisabled, createMultipleDisabled, getByIdDisabled, listDisabled, deleteByIdDisabled, deleteByIdsDisabled, patchByIdDisabled,
                    patchMultipleDisabled, deleteByQueryDisabled, patchByQueryDisabled, getByIdsDisabled, patchByIdsDisabled, updateByIdDisabled, updateMultipleDisabled);
        }
    }

}
