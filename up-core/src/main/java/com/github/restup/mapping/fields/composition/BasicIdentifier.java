package com.github.restup.mapping.fields.composition;

class BasicIdentifier implements Identifier {

    private final static Identifier TRUE = new BasicIdentifier(true);
    private final static Identifier FALSE = new BasicIdentifier(false);

    private final boolean clientGeneratedIdentifierPermitted;

    BasicIdentifier(boolean clientGeneratedIdentifierPermitted) {
        this.clientGeneratedIdentifierPermitted = clientGeneratedIdentifierPermitted;
    }

    public static Identifier of(boolean clientGeneratedIdentifierPermitted) {
        if (clientGeneratedIdentifierPermitted) {
            return TRUE;
        }
        return FALSE;
    }

    @Override
    public boolean isClientGeneratedIdentifierPermitted() {
        return clientGeneratedIdentifierPermitted;
    }

}
