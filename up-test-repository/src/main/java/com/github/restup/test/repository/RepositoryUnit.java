package com.github.restup.test.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.restup.annotations.model.CreateStrategy;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.repository.ResourceRepositoryOperations;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.github.restup.test.resource.RelativeTestResource;
import com.github.restup.util.Assert;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;

public class RepositoryUnit {

    private static ObjectMapper mapper;

    private RepositoryUnit() {
        super();
    }

    public static ObjectMapper getMapper() {
        if (mapper == null) {
            synchronized (RepositoryUnit.class) {
                if (mapper == null) {
                    mapper = new ObjectMapper();
                }
            }
        }
        return mapper;
    }

    public static void load(ResourceRegistry registry, String fileName) {
        loader().registry(registry)
                .fileName(fileName)
                .load();
    }

    public static Loader loader() {
        return new Loader();
    }

    public static final class Loader {

        private ObjectMapper mapper;
        private String fileName;
        private ResourceRegistry registry;
        private Class<?> relativeTo;

        private Loader me() {
            return this;
        }

        public Loader mapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return me();
        }

        public Loader fileName(String fileName) {
            this.fileName = fileName;
            return me();
        }

        public Loader registry(ResourceRegistry registry) {
            this.registry = registry;
            return me();
        }

        public Loader relativeTo(Class<?> relativeTo) {
            this.relativeTo = relativeTo;
            return me();
        }

        public void load(String fileName) {
            Assert.notEmpty(fileName, "fileName is required");
            fileName(fileName).load();
        }

        public void load() {
            if (registry == null) {
                throw new IllegalStateException("registry is required");
            }
            if (mapper == null) {
                mapper = getMapper();
            }
            if (fileName == null) {
                fileName = RelativeTestResource.getCallingMethodName();
            }
            if (relativeTo == null) {
                relativeTo = RelativeTestResource.getClassFromStack();
            }
            RelativeTestResource contents = RelativeTestResource.dump(relativeTo, fileName);
            load(contents);
        }

        private void load(RelativeTestResource contents) {
            try {
                JsonNode node = mapper.readTree(contents.getContentAsString());
                load(node);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void load(JsonNode node) {
            if (node instanceof ObjectNode) {
                Iterator<Entry<String, JsonNode>> iterator = node.fields();
                while (iterator.hasNext()) {
                    Entry<String, JsonNode> e = iterator.next();
                    Resource<?, ?> resource = registry.getResourceByPluralName(e.getKey());
                    Assert.notNull(resource, e.getKey() + " is not a valid resource");
                    if (e.getValue() instanceof ArrayNode) {
                        loadAll(resource, (ArrayNode) e.getValue());
                    } else {
                        error(e.getKey() + " must be an array");
                    }
                }
            } else {
                error("Top level object expected");
            }
        }

        private void error(String error) {
            throw new IllegalStateException(error);
        }

        private void loadAll(Resource<?, ?> resource, ArrayNode value) {
            Iterator<JsonNode> iterator = value.elements();
            while (iterator.hasNext()) {
                load(resource, iterator.next());
            }
        }

        private <T> void load(Resource<T, ?> resource, JsonNode node) {
            T t = null;
            try {
				t = mapper.treeToValue(node, resource.getClassType());
			} catch (JsonProcessingException e) {
				throw new AssertionError("Unable to deserialize "+resource, e);
			}
            // we have to validate the object to handle Untyped Objects (deserialized to map & lose type details)
            Resource.validate(resource, t);

            ResourceRepositoryOperations repository = resource.getRepositoryOperations();
            RequestObjectFactory factory = resource.getRegistry().getSettings().getRequestObjectFactory();
            CreateRequest<T> request = factory
                .getCreateRequest(resource, t, Collections.emptyList(), Collections.emptyList(),
                    null, CreateStrategy.CREATED);
            repository.create(request);
        }
    }
    
}
