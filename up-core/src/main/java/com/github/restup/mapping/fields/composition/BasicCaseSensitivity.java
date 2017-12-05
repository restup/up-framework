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

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    public String getSearchField() {
        return searchField;
    }

    public boolean isLowerCased() {
        return lowerCased;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BasicCaseSensitivity that = (BasicCaseSensitivity) o;
        return caseInsensitive == that.caseInsensitive &&
                lowerCased == that.lowerCased &&
                Objects.equals(searchField, that.searchField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseInsensitive, searchField, lowerCased);
    }

}
