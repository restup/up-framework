package com.github.restup.mapping.fields.composition;

import java.util.Objects;

class BasicCaseSensitivity implements CaseSensitivity {

    private final boolean caseInsensitive;
    private final String searchField;
    private final boolean lowerCased;

    BasicCaseSensitivity(boolean caseInsensitive, String searchField, boolean lowerCased) {
        this.caseInsensitive = caseInsensitive;
        this.searchField = searchField;
        this.lowerCased = lowerCased;
    }

    @Override
    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    @Override
    public String getSearchField() {
        return searchField;
    }

    @Override
    public boolean isLowerCased() {
        return lowerCased;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (! ( o instanceof BasicCaseSensitivity )) {
            return false;
        }
        BasicCaseSensitivity that = (BasicCaseSensitivity) o;
        return caseInsensitive == that.caseInsensitive &&
                lowerCased == that.lowerCased &&
                Objects.equals(searchField, that.searchField);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(caseInsensitive, searchField, lowerCased);
    }

}
