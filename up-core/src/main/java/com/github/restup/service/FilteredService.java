package com.github.restup.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import com.github.restup.annotations.filter.PostBulkCreateFilter;
import com.github.restup.annotations.filter.PostBulkDeleteFilter;
import com.github.restup.annotations.filter.PostBulkUpdateFilter;
import com.github.restup.annotations.filter.PostCreateFilter;
import com.github.restup.annotations.filter.PostDeleteByQueryFilter;
import com.github.restup.annotations.filter.PostDeleteFilter;
import com.github.restup.annotations.filter.PostListFilter;
import com.github.restup.annotations.filter.PostReadFilter;
import com.github.restup.annotations.filter.PostUpdateByQueryFilter;
import com.github.restup.annotations.filter.PostUpdateFilter;
import com.github.restup.annotations.filter.PreBulkCreateFilter;
import com.github.restup.annotations.filter.PreBulkDeleteFilter;
import com.github.restup.annotations.filter.PreBulkUpdateFilter;
import com.github.restup.annotations.filter.PreCreateFilter;
import com.github.restup.annotations.filter.PreDeleteByQueryFilter;
import com.github.restup.annotations.filter.PreDeleteFilter;
import com.github.restup.annotations.filter.PreListFilter;
import com.github.restup.annotations.filter.PreReadFilter;
import com.github.restup.annotations.filter.PreUpdateByQueryFilter;
import com.github.restup.annotations.filter.PreUpdateFilter;
import com.github.restup.annotations.operations.BulkCreateResource;
import com.github.restup.annotations.operations.BulkDeleteResource;
import com.github.restup.annotations.operations.BulkUpdateResource;
import com.github.restup.annotations.operations.CreateResource;
import com.github.restup.annotations.operations.DeleteResource;
import com.github.restup.annotations.operations.DeleteResourceByQuery;
import com.github.restup.annotations.operations.ListResource;
import com.github.restup.annotations.operations.ReadResource;
import com.github.restup.annotations.operations.UpdateResource;
import com.github.restup.annotations.operations.UpdateResourceByQuery;
import com.github.restup.registry.Resource;
import com.github.restup.registry.settings.ServiceMethodAccess;
import com.github.restup.repository.DefaultBulkRepository;
import com.github.restup.util.Assert;
import com.github.restup.util.ReflectionUtils;

/**
 * {@link FilteredService} is a {@link ResourceService} which executes annotated repository methods preceded by annotated filter methods and followed by annotated filter methods.
 *
 * @author abuttaro
 */
public class FilteredService extends MethodCommandOperations implements ResourceServiceOperations {

    public FilteredService(Resource<?, ?> resource, Object repository, Object... filters) {
        super(new FilteredServiceMethodCommandOperationFactory(resource, repository, filters));
    }

    public static class FilteredServiceMethodCommandOperationFactory implements MethodCommandOperationFactory {

        private final Resource<?, ?> resource;
        private final Object repository;
        private final Object[] filters;
        private final ServiceMethodAccess access;
        private final DefaultBulkRepository<?,?> defaultBulkRepo;

        public FilteredServiceMethodCommandOperationFactory(Resource<?, ?> resource, Object repository, Object... filters) {
            Assert.notNull(resource, "resource is required");
            Assert.notNull(repository, "operations is required");
            this.resource = resource;
            this.repository = repository;
            this.filters = filters;
            this.access = resource.getServiceMethodAccess();

            defaultBulkRepo = new DefaultBulkRepository<>(); // TODO

        }

        /**
         * Determines whether the repoAnnotation exists on the operations. If it does, a
         * {@link FilteredServiceMethodCommand} is returned If it does not, an
         * {@link UnsupportedMethodCommand} is returned
         * 
         * @param resource filter is applied to
         * @param operation name
         * @param repoAnnotation operation annotation
         * @param preAnnotation pre operation annotation
         * @param postAnnotation post operation annotation
         * @param disabledViaAccessSettings true if disabled
         * @param repositories available repositories for operation
         * @return methodCommand
         */
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
            return new FilteredServiceMethodCommand(resource, pair.getValue(), pair.getKey(), repoAnnotation, preAnnotation, postAnnotation, filters);
        }

        protected Pair<Method, Object> findAnnotatedRepositoryAndMethod(boolean disabledViaAccessSettings, Class<? extends Annotation> repoAnnotation, Object... repositories) {
            if (!disabledViaAccessSettings) {
                Method m = null;
                for (Object repository : repositories) {
                    m = ReflectionUtils.findAnnotatedMethod(repository.getClass(), repoAnnotation);
                    if (m != null) {
                        return new ImmutablePair<>(m, repository);
                    }
                }
            }
            return null;
        }

        @Override
        public MethodCommand<?> getCreateOperation() {
            return getMethod(resource, "CREATE", CreateResource.class, PreCreateFilter.class, PostCreateFilter.class, access.isCreateDisabled(), repository);
        }

        @Override
        public MethodCommand<?> getUpdateOperation() {
            return getMethod(resource, "UPDATE", UpdateResource.class, PreUpdateFilter.class, PostUpdateFilter.class, access.isPatchByIdDisabled(), repository);
        }

        @Override
        public MethodCommand<?> getDeleteOperation() {
            return getMethod(resource, "DELETE", DeleteResource.class, PreDeleteFilter.class, PostDeleteFilter.class, access.isDeleteByIdDisabled(), repository);
        }

        @Override
        public MethodCommand<?> getBulkUpdateOperation() {
            return getMethod(resource, "BULK_UPDATE", BulkUpdateResource.class, PreBulkUpdateFilter.class, PostBulkUpdateFilter.class, access.isPatchMultipleDisabled(), repository, defaultBulkRepo);
        }

        @Override
        public MethodCommand<?> getBulkCreateOperation() {
            return getMethod(resource, "BULK_CREATE", BulkCreateResource.class, PreBulkCreateFilter.class, PostBulkCreateFilter.class, access.isCreateMultipleDisabled(), repository, defaultBulkRepo);
        }

        @Override
        public MethodCommand<?> getBulkDeleteOperation() {
            return getMethod(resource, "BULK_DELETE", BulkDeleteResource.class, PreBulkDeleteFilter.class, PostBulkDeleteFilter.class, access.isDeleteByIdsDisabled(), repository, defaultBulkRepo);
        }

        @Override
        public MethodCommand<?> getDeleteByQueryOperation() {
            return getMethod(resource, "FILTERED_DELETE", DeleteResourceByQuery.class, PreDeleteByQueryFilter.class, PostDeleteByQueryFilter.class, access.isDeleteByQueryDisabled(), repository, defaultBulkRepo);
        }

        @Override
        public MethodCommand<?> getUpdateByQueryOperation() {
            return getMethod(resource, "FILTERED_UPDATE", UpdateResourceByQuery.class, PreUpdateByQueryFilter.class, PostUpdateByQueryFilter.class, access.isPatchByQueryDisabled(), repository, defaultBulkRepo);
        }

        @Override
        public MethodCommand<?> getListOperation() {
            return getMethod(resource, "LIST", ListResource.class, PreListFilter.class, PostListFilter.class, access.isListDisabled(), repository);
        }

        @Override
        public MethodCommand<?> getFindOperation() {
            return getMethod(resource, "FIND", ReadResource.class, PreReadFilter.class, PostReadFilter.class, access.isGetByIdDisabled(), repository);
        }

    }
}
