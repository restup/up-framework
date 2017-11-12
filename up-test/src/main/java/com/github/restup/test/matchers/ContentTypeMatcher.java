package com.github.restup.test.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public final class ContentTypeMatcher extends BaseMatcher<String[]> {

    private final String contentType;

    public ContentTypeMatcher(String contentType) {
        this.contentType = contentType;
    }

    public boolean matches(Object item) {
        if (item instanceof String[]) {
            String[] arr = (String[]) item;
            String[] parts = arr[0].split(";");
            return parts[0].equals(contentType);
        }
        return false;
    }

    public void describeTo(Description description) {
        description.appendText("Content-Type=").appendValue(contentType);
    }
}
