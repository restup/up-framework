package com.github.restup.test.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.restup.bind.converter.ParameterConverter;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.repository.ResourceRepositoryOperations;
import com.github.restup.service.model.request.CreateRequest;
import com.github.restup.service.model.request.RequestObjectFactory;
import com.github.restup.test.resource.RelativeTestResource;
import com.github.restup.util.Assert;
import com.github.restup.util.ReflectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;

public class RepositoryUnit {

    private static ObjectMapper mapper;

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
            if ( mapper == null ) {
                mapper = getMapper();
            }
            if ( fileName == null ) {
                fileName = RelativeTestResource.getCallingStackElement().getMethodName();
            }
            if ( registry == null ) {
                registry = ResourceRegistry.getInstance();
            }
            if ( relativeTo == null ) {
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
            if ( node instanceof  ObjectNode ) {
                ObjectNode o = (ObjectNode) node;
                Iterator<Entry<String,JsonNode>> iterator = node.fields();
                while ( iterator.hasNext() ) {
                    Entry<String,JsonNode> e = iterator.next();
                    Resource<?,?> resource = registry.getResourceByPluralName(e.getKey());
                    Assert.notNull(resource, e.getKey()+" is not a valid resource");
                    if ( e.getValue() instanceof ArrayNode ) {
                        loadAll(resource, (ArrayNode) e.getValue());
                    } else {
                        error(e.getKey()+" must be an array");
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
            while ( iterator.hasNext() ) {
                load(resource, iterator.next());
            }
        }

        private <T> void load(Resource<T, ?> resource, JsonNode node) {
            T t = readValue(resource, node);
            ResourceRepositoryOperations repository = resource.getRepositoryOperations();
            RequestObjectFactory factory = resource.getRegistry().getSettings().getRequestObjectFactory();
            CreateRequest<T> request = factory.getCreateRequest(resource, t, Collections.EMPTY_LIST, Collections.EMPTY_LIST, null);
            repository.create(request);
        }

        private <T> T readValue(Resource<T, ?> resource, JsonNode node) {
            T result = ReflectionUtils.newInstance(resource.getMapping().getType());
            Iterator<Entry<String,JsonNode>> iterator = node.fields();
            while ( iterator.hasNext() ) {
                Entry<String,JsonNode> e = iterator.next();
                String persistentName = e.getKey();
                MappedField f = resource.findPersistedField(persistentName);
                if ( f != null ) {
                    Object value = null;
                    if ( e.getValue().isArray() || e.getValue().isObject() ) {
                        //TODO complex types
                    } else {
                        ParameterConverter converter = registry.getSettings().getParameterConverterFactory().getConverter(String.class, f.getType());
                        value = converter.convert(null, e.getValue().textValue(), null);
                    }
                    f.writeValue(result, value);
                }
            }
            return result;
        }
    }
}
