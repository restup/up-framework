package com.github.restup.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class ApiRequestTest {

    private ApiRequest test(String path, Object... args) {
        ApiRequest.Builder b = new ApiRequest.Builder(path, args);
        return b.build();
    }

    @Test
    public void testUrl() {
        ApiRequest request = test("/foo/bar?q=b", 1);
        assertThat(request.getUrl(), is("/foo/bar?q=b"));
    }

    @Test
    public void testUrlPathVarDefault() {
        ApiRequest request = test("/foo/{bar}?", 1, 2, 3, 4, 5);
        assertThat(request.getUrl(), is("/foo/1"));
    }

    private ApiRequest override(Object... args) {
        ApiRequest.Builder b = new ApiRequest.Builder("/{accountId}/{organizationId}/foo/{bar}", 1, 2, 3);
        b.pathArgs(args);
        return b.build();
    }

    @Test
    public void testUrlMultiplePathVarOverride() {
        assertThat(override().getUrl(), is("/1/2/foo/3"));
        assertThat(override("a").getUrl(), is("/1/2/foo/a"));
        assertThat(override("a", "b").getUrl(), is("/1/a/foo/b"));
        assertThat(override("a", "b", "c").getUrl(), is("/a/b/foo/c"));
    }

    @Test
    public void testQueryString() {
        ApiRequest.Builder b = new ApiRequest.Builder("/{accountId}/{organizationId}/foo/{bar}?v=1", 1, 2, 3);
        b.pathArgs("a");
        b.query("?foo=x&bar=y&baz=z&foo=x2");
        assertThat(b.build().getUrl(), is("/1/2/foo/a?foo=x&foo=x2&bar=y&baz=z&v=1"));
    }
}
