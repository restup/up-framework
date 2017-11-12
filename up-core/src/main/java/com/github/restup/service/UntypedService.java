package com.github.restup.service;

import com.github.restup.registry.Resource;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * A Resource may define an annotated service without a repository.
 * In this case, a {@link FilteredService} is not used.  However,
 * The service may still be annotated and must be wrapped by UntypedService
 * to execute its annotated methods correctly
 * @param <T>
 * @param <ID>
 */
public class UntypedService<T, ID extends Serializable> extends MethodCommandOperations<T,ID> implements ResourceService<T, ID> {

    public UntypedService(Resource<?, ?> resource, Object service) {
        super(new ServiceMethodCommandOperationFactory(resource, service));
    }

    public static class ServiceMethodCommandOperationFactory extends FilteredService.FilteredServiceMethodCommandOperationFactory {

        public ServiceMethodCommandOperationFactory(Resource<?, ?> resource, Object service) {
            super(resource, service);
        }

        /**
         * Determines whether the repoAnnotation exists on the operations.
         * If it does, a {@link FilteredServiceMethodCommand} is returned
         * If it does not, an {@link UnsupportedMethodCommand} is returned
         *
         * @return
         */
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
