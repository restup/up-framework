package com.github.restup.service;

import com.github.restup.annotations.filter.*;
import com.github.restup.annotations.operations.*;
import com.github.restup.registry.Resource;
import com.github.restup.registry.settings.ServiceMethodAccess;
import com.github.restup.repository.DefaultBulkRepository;
import com.github.restup.util.Assert;
import com.github.restup.util.ReflectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * {@link FilteredService} is a {@link ResourceService} which executes annotated repository methods
 * preceded by annotated filter methods and followed by annotated filter methods.
 *
 * @author abuttaro
 */
public class FilteredService extends MethodCommandOperations implements ResourceServiceOperations {

    private final Object[] filters;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public FilteredService(Resource<?, ?> resource, Object repository, Object... filters) {
        super(new FilteredServiceMethodCommandOperationFactory(resource, repository, filters));
        this.filters = filters;
    }

    public static class FilteredServiceMethodCommandOperationFactory implements MethodCommandOperationFactory {

        private final Resource<?, ?> resource;
        private final Object repository;
        private final Object[] filters;
        private final ServiceMethodAccess access;
        private final DefaultBulkRepository defaultBulkRepo;

        public FilteredServiceMethodCommandOperationFactory(Resource<?, ?> resource, Object repository, Object... filters) {
            Assert.notNull(resource, "resource is required");
            Assert.notNull(repository, "operations is required");
            this.resource = resource;
            this.repository = repository;
            this.filters = filters;
            this.access = resource.getServiceAccess();

            defaultBulkRepo = new DefaultBulkRepository(); // TODO

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
            return new FilteredServiceMethodCommand(resource, pair.getValue(), pair.getKey(), repoAnnotation, preAnnotation, postAnnotation, filters);
        }

        protected Pair<Method,Object> findAnnotatedRepositoryAndMethod(boolean disabledViaAccessSettings, Class<? extends Annotation> repoAnnotation, Object... repositories) {
            if (!disabledViaAccessSettings) {
                Method m = null;
                for (Object repository : repositories) {
                    m = ReflectionUtils.findAnnotatedMethod(repository.getClass(), repoAnnotation);
                    if (m != null) {
                        return new ImmutablePair(m, repository);
                    }
                }
            }
            return null;
        }

        public MethodCommand getCreateOperation() {
            return getMethod(resource, "CREATE", CreateResource.class, PreCreateFilter.class, PostCreateFilter.class, access.isCreateDisabled(), repository);
        }

        public MethodCommand getUpdateOperation() {
            return getMethod(resource, "UPDATE", UpdateResource.class, PreUpdateFilter.class, PostUpdateFilter.class, access.isPatchByIdDisabled(), repository);
        }

        public MethodCommand getDeleteOperation() {
            return getMethod(resource, "DELETE", DeleteResource.class, PreDeleteFilter.class, PostDeleteFilter.class, access.isDeleteByIdDisabled(), repository);
        }

        public MethodCommand getBulkUpdateOperation() {
            return getMethod(resource, "BULK_UPDATE", BulkUpdateResource.class, PreBulkUpdateFilter.class, PostBulkUpdateFilter.class, access.isPatchMultipleDisabled(), repository, defaultBulkRepo);
        }

        public MethodCommand getBulkCreateOperation() {
            return getMethod(resource, "BULK_CREATE", BulkCreateResource.class, PreBulkCreateFilter.class, PostBulkCreateFilter.class, access.isCreateMultipleDisabled(), repository, defaultBulkRepo);
        }

        public MethodCommand getBulkDeleteOperation() {
            return getMethod(resource, "BULK_DELETE", BulkDeleteResource.class, PreBulkDeleteFilter.class, PostBulkDeleteFilter.class, access.isDeleteByIdsDisabled(), repository, defaultBulkRepo);
        }

        public MethodCommand getDeleteByQueryOperation() {
            return getMethod(resource, "FILTERED_DELETE", DeleteResourceByQuery.class, PreDeleteByQueryFilter.class, PostDeleteByQueryFilter.class, access.isDeleteByQueryDisabled(), repository, defaultBulkRepo);
        }

        public MethodCommand getUpdateByQueryOperation() {
            return getMethod(resource, "FILTERED_UPDATE", UpdateResourceByQuery.class, PreUpdateByQueryFilter.class, PostUpdateByQueryFilter.class, access.isPatchByQueryDisabled(), repository, defaultBulkRepo);
        }

        public MethodCommand getListOperation() {
            return getMethod(resource, "LIST", ListResource.class, PreListFilter.class, PostListFilter.class, access.isListDisabled(), repository);
        }

        public MethodCommand getFindOperation() {
            return getMethod(resource, "FIND", ReadResource.class, PreReadFilter.class, PostReadFilter.class, access.isGetByIdDisabled(), repository);
        }

    }
}
