package com.github.restup.controller.request.parser.path;

import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRelationship;
import java.util.Arrays;
import java.util.List;

public interface RequestPathParserResult {


    static Builder builder() {
        return new Builder();
    }

    Resource<?, ?> getResource();

    Resource<?, ?> getRelationship();

    ResourceRelationship<?, ?, ?, ?> getResourceRelationship();

    List<?> getIds();

    class Builder {

        protected Resource<?, ?> resource;
        protected Resource<?, ?> relationship;
        protected ResourceRelationship<?, ?, ?, ?> resourceRelationship;
        protected List<?> ids;

        private Builder() {

        }

        public Builder me() {
            return this;
        }

        public Builder resource(Resource<?, ?> resource) {
            this.resource = resource;
            return me();
        }

        public Builder relationship(Resource<?, ?> relationship) {
            this.relationship = relationship;
            return me();
        }

        public Builder resourceRelationship(ResourceRelationship<?, ?, ?, ?> resourceRelationship) {
            this.resourceRelationship = resourceRelationship;
            return me();
        }

        public Builder ids(Object... ids) {
            return ids(Arrays.asList(ids));
        }

        public Builder ids(List<?> ids) {
            this.ids = ids;
            return me();
        }

        public RequestPathParserResult build() {
            return new BasicRequestPathParserResult(resource, relationship, resourceRelationship,
                ids);
        }
    }
}
