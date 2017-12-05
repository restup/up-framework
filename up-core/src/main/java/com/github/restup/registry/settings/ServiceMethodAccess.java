package com.github.restup.registry.settings;

/**
 * Settings which define which methods are exposed internally by service implementations. Disabling these service will disallow executing them internally as well as via http endpoints. However, if enabled, they may still be disabled by {@link ControllerMethodAccess}. By default all are enabled.
 */
public class ServiceMethodAccess {

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

    public ServiceMethodAccess(boolean createDisabled, boolean createMultipleDisabled, boolean getByIdDisabled, boolean listDisabled, boolean deleteByIdDisabled, boolean deleteByIdsDisabled, boolean patchByIdDisabled, boolean patchMultipleDisabled, boolean deleteByQueryDisabled, boolean patchByQueryDisabled) {
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

    public boolean isCreateDisabled() {
        return createDisabled;
    }

    public boolean isCreateMultipleDisabled() {
        return createMultipleDisabled;
    }

    public boolean isGetByIdDisabled() {
        return getByIdDisabled;
    }

    public boolean isListDisabled() {
        return listDisabled;
    }

    public boolean isDeleteByIdDisabled() {
        return deleteByIdDisabled;
    }

    public boolean isDeleteByIdsDisabled() {
        return deleteByIdsDisabled;
    }

    public boolean isPatchByIdDisabled() {
        return patchByIdDisabled;
    }

    public boolean isPatchMultipleDisabled() {
        return patchMultipleDisabled;
    }

    public boolean isDeleteByQueryDisabled() {
        return deleteByQueryDisabled;
    }

    public boolean isPatchByQueryDisabled() {
        return patchByQueryDisabled;
    }

    public static class Builder extends AbstractBuilder<Builder, ServiceMethodAccess> {

    }

    static abstract class AbstractBuilder<T extends AbstractBuilder<T, R>, R extends ServiceMethodAccess> {

        boolean createDisabled;
        boolean createMultipleDisabled;
        boolean getByIdDisabled;
        boolean listDisabled;
        boolean deleteByIdDisabled;
        boolean deleteByIdsDisabled;
        boolean patchByIdDisabled;
        boolean patchMultipleDisabled;
        boolean deleteByQueryDisabled;
        boolean patchByQueryDisabled;

        @SuppressWarnings("unchecked")
        protected T me() {
            return (T) this;
        }

        public T setAllDisabled(boolean b) {
            return setCreateDisabled(b)
                    .setCreateMultipleDisabled(b)
                    .setGetByIdDisabled(b)
                    .setListDisabled(b)
                    .setDeleteByIdDisabled(b)
                    .setDeleteByIdsDisabled(b)
                    .setDeleteByQueryDisabled(b)
                    .setPatchByIdDisabled(b)
                    .setPatchMultipleDisabled(b)
                    .setPatchByQueryDisabled(b);
        }

        public T setAllEnabled() {
            return setAllDisabled(false);
        }

        public T setAllDisabled() {
            return setAllDisabled(true);
        }

        public T setCreateDisabled(boolean createDisabled) {
            this.createDisabled = createDisabled;
            return me();
        }

        public T setCreateMultipleDisabled(boolean createMultipleDisabled) {
            this.createMultipleDisabled = createMultipleDisabled;
            return me();
        }

        public T setGetByIdDisabled(boolean getByIdDisabled) {
            this.getByIdDisabled = getByIdDisabled;
            return me();
        }

        public T setListDisabled(boolean listDisabled) {
            this.listDisabled = listDisabled;
            return me();
        }

        public T setDeleteByIdDisabled(boolean deleteByIdDisabled) {
            this.deleteByIdDisabled = deleteByIdDisabled;
            return me();
        }

        public T setDeleteByIdsDisabled(boolean deleteByIdsDisabled) {
            this.deleteByIdsDisabled = deleteByIdsDisabled;
            return me();
        }

        public T setDeleteByQueryDisabled(boolean deleteByQueryDisabled) {
            this.deleteByQueryDisabled = deleteByQueryDisabled;
            return me();
        }

        public T setPatchByIdDisabled(boolean patchByIdDisabled) {
            this.patchByIdDisabled = patchByIdDisabled;
            return me();
        }

        public T setPatchMultipleDisabled(boolean patchMultipleDisabled) {
            this.patchMultipleDisabled = patchMultipleDisabled;
            return me();
        }

        public T setPatchByQueryDisabled(boolean patchByQueryDisabled) {
            this.patchByQueryDisabled = patchByQueryDisabled;
            return me();
        }

        public ServiceMethodAccess build() {
            return new ServiceMethodAccess(createDisabled, createMultipleDisabled, getByIdDisabled, listDisabled, deleteByIdDisabled, deleteByIdsDisabled, patchByIdDisabled, patchMultipleDisabled, deleteByQueryDisabled, patchByQueryDisabled);
        }
    }

}