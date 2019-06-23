package com.github.restup.test.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public final class ContentTypeMatcher extends BaseMatcher<String> {

    private final String contentType;

    public ContentTypeMatcher(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public boolean matches(Object item) {
        if (item instanceof String) {
            String header = (String) item;
            String[] parts = header.split(";");
            return parts[0].equals(contentType);
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Content-Type=").appendValue(contentType);
    }
}
