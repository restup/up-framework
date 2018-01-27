package com.github.restup.registry.settings;

public abstract class AbstractMethodAccessBuilder<T extends AbstractMethodAccessBuilder<T, R>, R extends ServiceMethodAccess> {

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
        return new BasicServiceMethodAccess(createDisabled, createMultipleDisabled, getByIdDisabled, listDisabled, deleteByIdDisabled, deleteByIdsDisabled, patchByIdDisabled, patchMultipleDisabled, deleteByQueryDisabled, patchByQueryDisabled);
    }
}