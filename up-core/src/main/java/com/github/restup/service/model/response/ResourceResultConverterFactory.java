package com.github.restup.service.model.response;

import com.github.restup.annotations.operations.*;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Factory for providing correct {@link ResourceResultConverter}
 * implementation for appropriate annotated methods
 */
public class ResourceResultConverterFactory {

    private static volatile ResourceResultConverterFactory instance;

    private final NoOpResourceResultConverter noOpResourceResultConverter = new NoOpResourceResultConverter();
    private final Map<Class<? extends Annotation>, ResourceResultConverter> converters;

    public static ResourceResultConverterFactory getInstance() {
        if (instance == null) {
            synchronized (ResourceResultConverterFactory.class) {
                if (instance == null) {
                    instance = new ResourceResultConverterFactory();
                }
            }
        }
        return instance;
    }

    private ResourceResultConverterFactory() {
        Map map = new IdentityHashMap(10);
        PersistenceResultConverter persistenceResultConverter = new PersistenceResultConverter();
        PersistenceListResultConverter persistenceListResultConverter = new PersistenceListResultConverter();
        map.put(CreateResource.class, persistenceResultConverter);
        map.put(UpdateResource.class, persistenceResultConverter);
        map.put(DeleteResource.class, persistenceResultConverter);
        map.put(ListResource.class, new ListResourceResultConverter());
        map.put(ReadResource.class, new ReadResourceResultConverter());
        map.put(BulkCreateResource.class, persistenceListResultConverter);
        map.put(BulkUpdateResource.class, persistenceListResultConverter);
        map.put(BulkDeleteResource.class, persistenceListResultConverter);
        map.put(UpdateResourceByQuery.class, persistenceListResultConverter);
        map.put(DeleteResourceByQuery.class, persistenceListResultConverter);
        converters = Collections.unmodifiableMap(map);
    }

    public ResourceResultConverter getConverter(Class<? extends Annotation> annotation) {
        ResourceResultConverter converter = converters.get(annotation);
        return converter != null ? converter : noOpResourceResultConverter;
    }

    private final static class NoOpResourceResultConverter implements ResourceResultConverter {
        public Object convert(Object o) {
            return o;
        }
    }

    private final static class PersistenceResultConverter implements ResourceResultConverter {
        public Object convert(Object result) {
            if (result instanceof PersistenceResult) {
                return (PersistenceResult) result;
            } else {
                return new BasicPersistenceResult(result);
            }
        }
    }

    private final static class PersistenceListResultConverter implements ResourceResultConverter {
        public Object convert(Object result) {
            if (result instanceof PersistenceResult) {
                return (PersistenceResult) result;
            } else {
                List<?> list = asList(result);
                return new BasicPersistenceResult(list);
            }
        }
    }

    private final static class ListResourceResultConverter implements ResourceResultConverter {
        public Object convert(Object result) {
            if (result instanceof ReadResult) {
                return (ReadResult) result;
            } else {
                List<?> list = asList(result);
                return new BasicListResult(list);
            }
        }
    }

    private final static class ReadResourceResultConverter implements ResourceResultConverter {
        public Object convert(Object result) {
            if (result instanceof ReadResult) {
                return (ReadResult) result;
            } else {
                return new BasicReadResult(result);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<?> asList(Object result) {
        if (result instanceof List) {
            return (List) result;
        } else if (result instanceof Collection) {
            return new ArrayList((Collection) result);
        } else if (result != null) {
            return Arrays.asList(result);
        } else {
            return Collections.emptyList();
        }
    }

}
