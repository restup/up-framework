package com.github.restup.mapping.fields.composition;

class BasicIdentifier implements Identifier {

    private final static Identifier TRUE = new BasicIdentifier(true);
    private final static Identifier FALSE = new BasicIdentifier(false);

    private final boolean nonAutoGeneratedValuePermitted;

    BasicIdentifier(boolean nonAutoGeneratedValuePermitted) {
        this.nonAutoGeneratedValuePermitted = nonAutoGeneratedValuePermitted;
    }

    public static Identifier of(boolean nonAutoGeneratedValuePermitted) {
        if (nonAutoGeneratedValuePermitted) {
            return TRUE;
        }
        return FALSE;
    }

    @Override
    public boolean isNonAutoGeneratedValuePermitted() {
        return nonAutoGeneratedValuePermitted;
    }

}
