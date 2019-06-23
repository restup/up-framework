package com.github.restup.test.matchers;

import com.github.restup.test.ApiResponse;
import com.github.restup.test.ApiResponseFilter;
import com.github.restup.test.HttpStatus;
import java.util.regex.Pattern;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.text.MatchesPattern;

public final class LocationMatcher extends BaseMatcher<String> implements
    ApiResponseFilter<String[]> {

    private final MatchesPattern matchesPattern;
    private final HttpStatus httpStatus;


    public LocationMatcher(MatchesPattern matchesPattern, HttpStatus httpStatus) {
        this.matchesPattern = matchesPattern;
        this.httpStatus = httpStatus;
    }

    public LocationMatcher(Pattern pattern, HttpStatus httpStatus) {
        this(new MatchesPattern(pattern), httpStatus);
    }

    public static Matcher<String> matchesLocation(Pattern pattern, HttpStatus httpStatus) {
        return new LocationMatcher(pattern, httpStatus);
    }

    public static Matcher<String> matchesLocation(String regex, HttpStatus httpStatus) {
        return matchesLocation(Pattern.compile(regex), httpStatus);
    }

    public static Matcher<String> matchesLocationPath(String path) {
        return matchesLocationPath(path, HttpStatus.CREATED);
    }

    public static Matcher<String> matchesLocationPath(String path, HttpStatus httpStatus) {
        String s = path.startsWith("/") ? path.substring(1) : path;
        return matchesLocation(
            "^((http[s]?):\\/)\\/([^:\\/\\s]+)((\\/\\w+)*\\/)(" + s + "\\/)([\\w\\-]+)$",
            httpStatus);
    }

    @Override
    public boolean matches(Object item) {
        return matchesPattern.matches(item);
    }

    @Override
    public void describeTo(Description description) {
        matchesPattern.describeTo(description);
    }

    @Override
    public boolean accept(ApiResponse response) {
        return response.getStatus() == httpStatus.getHttpStatus();
    }
}
