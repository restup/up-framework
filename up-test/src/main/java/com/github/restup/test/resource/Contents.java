package com.github.restup.test.resource;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public interface Contents {

    static Contents of(byte[] bytes) {
        return new ByteArrayContents(bytes);
    }

    static Contents of(String bytes) {
        return new StringContents(bytes);
    }

    static Builder builder() {
        return new Builder();
    }

    String getContentAsString();

    byte[] getContentAsByteArray();

    static class Builder {

        private Class<?> relativeTo;
        private String name;
        private Contents contents;
        private String type;

        protected Builder me() {
            return this;
        }

        /**
         * Specifies the {@link Contents} as a byte array
         *
         * @param body of contents
         * @return this builder
         */
        public Builder contents(byte[] body) {
            return this.contents(new ByteArrayContents(body));
        }

        /**
         * Specifies the {@link Contents} as a String
         *
         * @param body of contents
         * @return this builder
         */
        public Builder contents(String body) {
            return this.contents(new StringContents(body));
        }

        public Builder contents(Contents bodyResource) {
            this.contents = bodyResource;
            return this.me();
        }

        /**
         * Optionally specifies a class used by {@link RelativeTestResource} indicating which class
         * the test resource is relative to
         *
         * @param relativeTo class
         * @return this builder
         */
        public Builder relativeTo(Class<?> relativeTo) {
            this.relativeTo = relativeTo;
            return this.me();
        }

        /**
         * Optionally names the contents.  If defaulting to a {@link RelativeTestResource}, name
         * will be used as name of the file
         *
         * @param name of contents
         * @return this builder
         */
        public Builder name(String name) {
            this.name = name;
            return this.me();
        }

        /**
         * Optionally indicates the type of contents.  If defaulting to a {@link
         * RelativeTestResource} type will be used as the extension.
         *
         * @param type of contents
         * @return this builder
         */
        public Builder type(String type) {
            this.type = type;
            return this.me();
        }

        public Contents build() {
            Contents result = this.contents;
            if (result == null) {
                String name = this.name;
                if (isBlank(this.name)) {
                    name = RelativeTestResource.getCallingMethodName();
                }
                String extension = this.type;
                if (isEmpty(extension)) {
                    extension = "txt";
                }
                result = new RelativeTestResource(this.relativeTo, null, name, extension);
            }
            return result;
        }

    }
    
}
