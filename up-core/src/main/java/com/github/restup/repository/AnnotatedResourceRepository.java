package com.github.restup.repository;

import com.github.restup.registry.Resource;
import com.github.restup.service.*;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * A resource repository may include annotated methods only.  In this case,
 * The repository may need to be wrapped with {@link AnnotatedResourceRepository} to
 * execute annotated methods with correct arguments.
 *
 */
public class AnnotatedResourceRepository extends MethodCommandOperations implements ResourceRepositoryOperations {

    private final Object repository;

    public AnnotatedResourceRepository(Resource<?,?> resource, Object repository) {
        super(new RepositoryMethodCommandOperationFactory(resource, repository));
        this.repository = repository;
    }

    /**
     * The original, wrapped repository instance
     * @return
     */
    public Object getRepository() {
        return repository;
    }

    public static class RepositoryMethodCommandOperationFactory extends FilteredService.FilteredServiceMethodCommandOperationFactory {

        public RepositoryMethodCommandOperationFactory(Resource<?, ?> resource, Object repository) {
            super(resource, repository);
        }

        @SuppressWarnings({"rawtypes"})
        protected MethodCommand<?> getMethod(Resource resource, String operation
                , Class<? extends Annotation> repoAnnotation
                , Class<? extends Annotation> preAnnotation
                , Class<? extends Annotation> postAnnotation
                , boolean disabledViaAccessSettings
                , Object... repositories) {
            Pair<Method,Object> pair = findAnnotatedRepositoryAndMethod(disabledViaAccessSettings, repoAnnotation, repositories);
            if (pair == null) {
                return new UnsupportedMethodCommand(resource, operation);
            }
            return new AnnotatedOperationMethodCommand(resource, pair.getValue(), pair.getKey(), repoAnnotation);
        }
    }
}
