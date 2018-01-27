package com.github.restup.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang3.tuple.Pair;

import com.github.restup.registry.Resource;

/**
 * A Resource may define an annotated service without a repository. In this case, a {@link FilteredService} is not used.  However, The service may still be annotated and must be wrapped by AnnotatedService to execute its annotated methods correctly
 */
public class AnnotatedService extends MethodCommandOperations implements ResourceServiceOperations {

    public AnnotatedService(Resource<?, ?> resource, Object service) {
        super(new ServiceMethodCommandOperationFactory(resource, service));
    }

    public static class ServiceMethodCommandOperationFactory extends FilteredService.FilteredServiceMethodCommandOperationFactory {

        public ServiceMethodCommandOperationFactory(Resource<?, ?> resource, Object service) {
            super(resource, service);
        }

        /**
         * Determines whether the repoAnnotation exists on the operations. If it does, a {@link FilteredServiceMethodCommand} is returned If it does not, an {@link UnsupportedMethodCommand} is returned
         */
        @Override
        @SuppressWarnings({"rawtypes"})
        protected MethodCommand<?> getMethod(Resource resource, String operation
                , Class<? extends Annotation> repoAnnotation
                , Class<? extends Annotation> preAnnotation
                , Class<? extends Annotation> postAnnotation
                , boolean disabledViaAccessSettings
                , Object... repositories) {
            Pair<Method, Object> pair = findAnnotatedRepositoryAndMethod(disabledViaAccessSettings, repoAnnotation, repositories);
            if (pair == null) {
                return new UnsupportedMethodCommand(resource, operation);
            }
            return new AnnotatedOperationMethodCommand(resource, pair.getValue(), pair.getKey(), repoAnnotation);
        }
    }

}
