package com.github.restup.test.resource;

import org.apache.commons.lang3.StringUtils;

public interface Contents {

    String getContentAsString();

    byte[] getContentAsByteArray();

    static Contents of(byte[] bytes) {
        return new ByteArrayContents(bytes);
    }

    static Contents of(String bytes) {
        return new StringContents(bytes);
    }
    
    static Builder builder() {
        return new Builder();
    }

    static class Builder {

        private Class<?> testClass;
        private String testName;
        private Contents contents;

        protected Builder me() {
            return this;
        }

        public Builder contents(byte[] body) {
            return contents(new ByteArrayContents(body));
        }

        public Builder contents(String body) {
            return contents(new StringContents(body));
        }

        public Builder contents(Contents bodyResource) {
            this.contents = bodyResource;
            return me();
        }

        public Builder testClass(Class<?> testClass) {
            this.testClass = testClass;
            return me();
        }

        public Builder testName(String testName) {
            this.testName = testName;
            return me();
        }

        public Contents build() {
            Contents result = this.contents;
            if (result == null ) {
                String name = testName;
                if (StringUtils.isBlank(testName)) {
                    name = RelativeTestResource.getCallingMethodName();
                }
                result = new RelativeTestResource(testClass, null, name, "txt");
            }
            return result;
        }
    
    }
    
}
