package com.github.restup.test;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import com.github.restup.test.resource.Contents;
import com.github.restup.test.resource.RelativeTestResource;

public interface ApiRequest {

    HttpMethod getMethod();

    Contents getBody();

    Map<String, String[]> getHeaders();

    boolean isHttps();

    String getUrl();
    
    static Builder builder(String path, Object... args) {
        return new Builder(path, args);
    }

    static class Builder extends AbstractApiRequestBuilder<Builder, String[]> {

        private final String path;
        private final Object[] defaultPathArgs;
        private HttpMethod method;
        private boolean https;
        private Object[] pathArgs;
        private Map<String, String[]> params = new LinkedHashMap<String, String[]>();

        private Builder(String path, Object... args) {
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

        @Override
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

            return new BasicApiRequest(method, getHeaders(), url.toString(), getBody(), https);
        }
    }
}
