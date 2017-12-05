package com.github.restup.test;

import static com.github.restup.test.RpcApiTest.HttpMethod;

import com.github.restup.test.resource.ByteArrayContents;
import com.github.restup.test.resource.Contents;
import com.github.restup.test.resource.RelativeTestResource;
import com.github.restup.test.resource.StringContents;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class ApiRequest {

    private final HttpMethod method;
    private final Map<String, String[]> headers;
    private final String url;
    private final Contents body;
    private final boolean https;

    private ApiRequest(HttpMethod method, Map<String, String[]> headers, String url, Contents body, boolean https) {
        this.method = method;
        this.headers = headers;
        this.url = url;
        this.body = body;
        this.https = https;
    }

    private ApiRequest(HttpMethod method, Map<String, String[]> headers, String url, byte[] body, boolean https) {
        this(method, headers, url, new ByteArrayContents(body), https);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Contents getBody() {
        return body;
    }

    public Map<String, String[]> getHeaders() {
        return headers;
    }

    public boolean isHttps() {
        return https;
    }

    public String getUrl() {
        return url;
    }

    abstract static class AbstractBuilder<B extends AbstractBuilder<B, H>, H> {

        protected Map<String, H> headers = new HashMap<String, H>();
        private Class<?> testClass;
        private String testName;
        private String testFileExtension;
        private Contents bodyResource;

        @SuppressWarnings({"unchecked"})
        protected B me() {
            return (B) this;
        }

        public B body(byte[] body) {
            return body(new ByteArrayContents(body));
        }

        public B body(String body) {
            return body(new StringContents(body));
        }

        public B body(Contents bodyResource) {
            this.bodyResource = bodyResource;
            return me();
        }

        public B testClass(Class<?> testClass) {
            this.testClass = testClass;
            return me();
        }

        public B testName(String testName) {
            this.testName = testName;
            return me();
        }

        public B testFileExtension(String testFileExtension) {
            this.testFileExtension = testFileExtension;
            return me();
        }

        protected void add(Map<String, String[]> map, String key, String... values) {
            if (StringUtils.isNotEmpty(key)) {
                String[] existing = map.get(key);
                if (existing == null) {
                    map.put(key, values);
                } else {
                    map.put(key, ArrayUtils.addAll(existing, values));
                }
            }
        }

        protected Map<String, H> getHeaders() {
            return headers;
        }

        protected abstract String getTestDir();

        protected boolean isDefaultTestResourceAllowed() {
            return StringUtils.isNotBlank(testName);
        }

        protected boolean hasConfiguredBody() {
            return bodyResource != null || isDefaultTestResourceAllowed();
        }

        public Contents getBody() {
            if (bodyResource != null) {
                return bodyResource;
            }
            if (isDefaultTestResourceAllowed()) {
                RelativeTestResource resource = new RelativeTestResource(testClass, getTestDir(), testName, testFileExtension);
                return resource;
            }
            return null;
        }

    }

    public static class Builder extends AbstractBuilder<Builder, String[]> {

        private final String path;
        private final Object[] defaultPathArgs;
        private HttpMethod method;
        private boolean https;
        private Object[] pathArgs;
        private Map<String, String[]> params = new LinkedHashMap<String, String[]>();

        public Builder(String path, Object... args) {
            this.path = path;
            this.defaultPathArgs = args;
        }

        @Override
        protected String getTestDir() {
            return RelativeTestResource.REQUESTS;
        }

        @Override
        protected boolean isDefaultTestResourceAllowed() {
            return super.isDefaultTestResourceAllowed() &&
                    (HttpMethod.POST == method
                            || HttpMethod.PATCH == method
                            || HttpMethod.PUT == method);
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return me();
        }

        public Builder https(boolean https) {
            this.https = https;
            return me();
        }

        public Builder pathArgs(Object... pathArgs) {
            this.pathArgs = pathArgs;
            return me();
        }

        public Builder header(String name, String... value) {
            add(headers, name, value);
            return me();
        }

        public Builder param(String name, String... value) {
            add(params, name, value);
            return me();
        }

        public Builder query(String queryString) {
            String s = queryString;
            if (s.charAt(0) == '?') {
                s = s.substring(1);
            }
            String[] arr = s.split("&");
            for (String a : arr) {
                String[] tuple = a.split("=");
                if (tuple.length == 2) {
                    param(tuple[0], tuple[1]);
                } else {
                    param(tuple[0], (String) null);
                }
            }
            return me();
        }

        private String getArg(int index) {
            int n = defaultPathArgs.length;
            if (index >= n) {
                throw new IllegalStateException("Insufficient default arguments");
            }
            int m = ArrayUtils.getLength(pathArgs);

            if (index >= n - m) {
                int i = index - (n - m);
                return pathArgs[i].toString();
            }

            return defaultPathArgs[index].toString();
        }

        public ApiRequest build() {
            if (method == null) {
                method = HttpMethod.GET;
            }
            StringBuilder url = new StringBuilder();
            String s = path;
            int q = s.indexOf('?');
            if (q > 0) {
                // add query params
                query(s.substring(q));
                //
                s = s.substring(0, q);
            }
            String[] parts = s.split("/");
            int i = 0;
            for (String part : parts) {
                if (part.length() > 0) {
                    url.append("/");
                    if (part.charAt(0) == '{' && part.charAt(part.length() - 1) == '}') {
                        url.append(getArg(i++));
                    } else {
                        url.append(part);
                    }
                }
            }

            if (!params.isEmpty()) {
                boolean first = true;
                for (Map.Entry<String, String[]> e : params.entrySet()) {
                    for (String p : e.getValue()) {
                        if (first) {
                            url.append("?");
                            first = false;
                        } else {
                            url.append("&");
                        }
                        url.append(e.getKey());
                        url.append("=");
                        url.append(p);
                    }
                }
            }

            return new ApiRequest(method, getHeaders(), url.toString(), getBody(), https);
        }
    }
}
