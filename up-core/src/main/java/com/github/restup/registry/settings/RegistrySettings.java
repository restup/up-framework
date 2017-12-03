package com.github.restup.registry.settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.restup.bind.DefaultMethodArgumentFactory;
import com.github.restup.bind.MethodArgumentFactory;
import com.github.restup.bind.converter.ParameterConverter;
import com.github.restup.bind.converter.ParameterConverterFactory;
import com.github.restup.errors.DefaultErrorFactory;
import com.github.restup.errors.ErrorFactory;
import com.github.restup.mapping.DefaultMappedClassFactory;
import com.github.restup.mapping.MappedClass;
import com.github.restup.mapping.MappedClassFactory;
import com.github.restup.mapping.fields.DefaultMappedFieldFactory;
import com.github.restup.mapping.fields.MappedField;
import com.github.restup.mapping.fields.MappedFieldBuilderVisitor;
import com.github.restup.mapping.fields.MappedFieldFactory;
import com.github.restup.mapping.fields.visitors.IdentityByConventionMappedFieldBuilderVisitor;
import com.github.restup.mapping.fields.visitors.JacksonMappedFieldBuilderVisitor;
import com.github.restup.path.AllResourcePathsProvider;
import com.github.restup.path.EmptyResourcePathsProvider;
import com.github.restup.path.ResourcePathsProvider;
import com.github.restup.query.Pagination;
import com.github.restup.registry.Resource;
import com.github.restup.registry.ResourceRegistry;
import com.github.restup.registry.ResourceRegistryRepository;
import com.github.restup.repository.RepositoryFactory;
import com.github.restup.service.filters.BulkOperationByQueryFilter;
import com.github.restup.service.filters.CaseInsensitiveSearchFieldFilter;
import com.github.restup.service.filters.ImmutableFieldValidationFilter;
import com.github.restup.service.filters.IncludeFilter;
import com.github.restup.service.filters.JavaxValidationFilter;
import com.github.restup.service.filters.NotFoundFilter;
import com.github.restup.service.filters.RelationshipValidationFilter;
import com.github.restup.service.filters.SequencedIdValidationFilter;
import com.github.restup.service.model.request.DefaultRequestObjectFactory;
import com.github.restup.service.model.request.RequestObjectFactory;

/**
 * Configures settings and implementations to be used by registry.
 */
public class RegistrySettings {

    private final static Logger log = LoggerFactory.getLogger(RegistrySettings.class);

    private final boolean primaryRegistry;
    private final ResourceRegistryRepository resourceRegistryMap;
    private final MappedClassFactory mappedClassFactory;
    private final String[] packagesToScan;
    private final MappedFieldFactory mappedFieldFactory;
    private final MappedFieldBuilderVisitor[] mappedFieldVisitors;
    private final Comparator<MappedField<?>> mappedFieldOrderComparator;

    private final ControllerMethodAccess defaultControllerMethodAccess;
    private final ServiceMethodAccess defaultServiceMethodAccess;
    private final Pagination defaultPagination;
    private final ResourcePathsProvider defaultSparseFieldsProvider;
    private final ResourcePathsProvider defaultRestrictedFieldsProvider;

    private final RepositoryFactory repositoryFactory;
    private final ErrorFactory errorFactory;
    private final RequestObjectFactory requestObjectFactory;
    private final MethodArgumentFactory methodArgumentFactory;
    private final ParameterConverterFactory parameterConverterFactory;
    private final Object[] defaultServiceFilters;
    private final String basePath;

    private RegistrySettings(boolean primaryRegistry
            , ResourceRegistryRepository resourceRegistryMap
            , MappedClassFactory mappedClassFactory
            , String[] packagesToScan
            , MappedFieldFactory mappedFieldFactory
            , MappedFieldBuilderVisitor[] mappedFieldVisitors
            , Comparator<MappedField<?>> mappedFieldOrderComparator
            , ControllerMethodAccess defaultControllerMethodAccess
            , ServiceMethodAccess defaultServiceMethodAccess
            , RepositoryFactory repositoryFactory
            , ErrorFactory errorFactory, RequestObjectFactory requestObjectFactory
            , MethodArgumentFactory methodArgumentFactory
            , ParameterConverterFactory parameterConverterFactory
            , Object[] defaultServiceFilters
            , Pagination defaultPagination
            , ResourcePathsProvider defaultSparseFieldsProvider
            , ResourcePathsProvider defaultRestrictedFieldsProvider
            , String basePath) {
        this.primaryRegistry = primaryRegistry;
        this.packagesToScan = packagesToScan;
        this.mappedFieldFactory = mappedFieldFactory;
        this.mappedFieldVisitors = mappedFieldVisitors;
        this.mappedFieldOrderComparator = mappedFieldOrderComparator;
        this.defaultControllerMethodAccess = defaultControllerMethodAccess;
        this.defaultServiceMethodAccess = defaultServiceMethodAccess;
        this.repositoryFactory = repositoryFactory;
        this.errorFactory = errorFactory;
        this.requestObjectFactory = requestObjectFactory;
        this.parameterConverterFactory = parameterConverterFactory;
        this.defaultServiceFilters = defaultServiceFilters;
        this.defaultPagination = defaultPagination;
        this.defaultSparseFieldsProvider = defaultSparseFieldsProvider;
        this.defaultRestrictedFieldsProvider = defaultRestrictedFieldsProvider;
        this.basePath = basePath;


        MappedClassFactory factory = mappedClassFactory;
        if (mappedClassFactory == null) {
            factory = new DefaultMappedClassFactory(this);
        }

        // wrap with operations
        RegistryOperations operations = new RegistryOperations(resourceRegistryMap, factory);
        this.mappedClassFactory = operations;
        this.resourceRegistryMap = operations;

        if (methodArgumentFactory == null) {
            this.methodArgumentFactory = new DefaultMethodArgumentFactory(this);
        } else {
            this.methodArgumentFactory = methodArgumentFactory;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public ResourceRegistryRepository getResourceRegistryMap() {
        return resourceRegistryMap;
    }

    public MappedClassFactory getMappedClassFactory() {
        return mappedClassFactory;
    }

    public String[] getPackagesToScan() {
        return packagesToScan;
    }

    public MappedFieldFactory getMappedFieldFactory() {
        return mappedFieldFactory;
    }

    public Comparator<MappedField<?>> getMappedFieldOrderComparator() {
        return mappedFieldOrderComparator;
    }

    public RepositoryFactory getRepositoryFactory() {
        return repositoryFactory;
    }

    public MappedFieldBuilderVisitor[] getMappedFieldVisitors() {
        return mappedFieldVisitors;
    }

    public ErrorFactory getErrorFactory() {
        return errorFactory;
    }

    public MethodArgumentFactory getMethodArgumentFactory() {
        return methodArgumentFactory;
    }

    public ParameterConverterFactory getParameterConverterFactory() {
        return parameterConverterFactory;
    }

    public ControllerMethodAccess getDefaultControllerAccess() {
        return defaultControllerMethodAccess;
    }

    public ServiceMethodAccess getDefaultServiceAccess() {
        return defaultServiceMethodAccess;
    }

    public boolean isPrimaryRegistry() {
        return primaryRegistry;
    }

    public Object[] getDefaultServiceFilters() {
        return defaultServiceFilters;
    }

    public RequestObjectFactory getRequestObjectFactory() {
        return requestObjectFactory;
    }

    public Pagination getDefaultPagination() {
        return defaultPagination;
    }

    public ResourcePathsProvider getDefaultRestrictedFieldsProvider() {
        return defaultRestrictedFieldsProvider;
    }

    public ResourcePathsProvider getDefaultSparseFieldsProvider() {
        return defaultSparseFieldsProvider;
    }

    public String getBasePath() {
        return basePath;
    }

    public static class Builder {

        private ParameterConverter<?, ?>[] parameterConverters;
        private boolean excludeFrameworkFilters;
        private boolean excludeDefaultParameterConverters;

        private boolean primaryRegistry;
        private ResourceRegistryRepository resourceRegistryMap;
        private MappedClassFactory mappedClassFactory;
        private String[] packagesToScan;
        private MappedFieldFactory mappedFieldFactory;
        private MappedFieldBuilderVisitor[] mappedFieldVisitors;
        private Comparator<MappedField<?>> mappedFieldOrderComparator;

        private ControllerMethodAccess defaultControllerMethodAccess;
        private ServiceMethodAccess defaultServiceMethodAccess;

        private RepositoryFactory repositoryFactory;
        private ErrorFactory errorFactory;
        private RequestObjectFactory requestObjectFactory;
        private MethodArgumentFactory methodArgumentFactory;
        private Object[] defaultServiceFilters;
        private Pagination defaultPagination;
        private Validator validator;
        private ResourcePathsProvider defaultSparseFieldsProvider;
        private ResourcePathsProvider defaultRestrictedFieldsProvider;
        private String basePath;

        private Builder me() {
            return this;
        }

        /**
         * If true, {@link ResourceRegistry} created using the resulting settings will be
         * made the singleton instance.
         *
         * @param singleton
         * @return
         */
        public Builder primaryRegistry(boolean singleton) {
            this.primaryRegistry = singleton;
            return me();
        }

        /**
         * Provided alternate storage for registry meta data
         *
         * @param resourceRegistryMap
         * @return
         */
        public Builder resourceRegistryMap(ResourceRegistryRepository resourceRegistryMap) {
            this.resourceRegistryMap = resourceRegistryMap;
            return me();
        }

        /**
         * This will behave as a default but will not override settings
         * explicitly passed elsewhere
         *
         * @param packagesToScan
         * @return
         */
        public Builder packagesToScan(String... packagesToScan) {
            this.packagesToScan = packagesToScan;
            return me();
        }

        /**
         * Comparator for defining sort order of {@link MappedClass#getAttributes()}
         *
         * @param mappedFieldOrderComparator
         * @return
         */
        public Builder mappedFieldOrderComparator(Comparator<MappedField<?>> mappedFieldOrderComparator) {
            this.mappedFieldOrderComparator = mappedFieldOrderComparator;
            return me();
        }

        /**
         * Provide an alternate implementation for creating {@link MappedField}
         *
         * @param mappedFieldFactory
         * @return
         */
        public Builder mappedFieldFactory(MappedFieldFactory mappedFieldFactory) {
            this.mappedFieldFactory = mappedFieldFactory;
            return me();
        }

        /**
         * If {@link #mappedFieldFactory(MappedFieldFactory)} is not overridden, {@link MappedFieldBuilderVisitor}
         * implementations may be specified to customize behavior of {@link DefaultMappedFieldFactory}
         *
         * @param visitors
         * @return
         */
        public Builder mappedFieldBuilderVisitors(MappedFieldBuilderVisitor... visitors) {
            this.mappedFieldVisitors = visitors;
            return me();
        }

        /**
         * Overrides factory for providing default {@link com.github.restup.repository.Repository} implementations.
         *
         * @param repositoryFactory
         * @return
         */
        public Builder repositoryFactory(RepositoryFactory repositoryFactory) {
            this.repositoryFactory = repositoryFactory;
            return me();
        }

        /**
         * Overrides factory for providing error objects
         *
         * @param errorFactory
         * @return
         */
        public Builder errorFactory(ErrorFactory errorFactory) {
            this.errorFactory = errorFactory;
            return me();
        }

        /**
         * @param methodArgumentFactory
         * @return
         */
        public Builder methodArgumentFactory(MethodArgumentFactory methodArgumentFactory) {
            this.methodArgumentFactory = methodArgumentFactory;
            return me();
        }

        /**
         * Defines default service method access for resources.  Resources may define their own.
         *
         * @param defaultServiceMethodAccess
         * @return
         */
        public Builder serviceMethodAccess(ServiceMethodAccess defaultServiceMethodAccess) {
            this.defaultServiceMethodAccess = defaultServiceMethodAccess;
            return me();
        }

        /**
         * Defines default service controller access for resources.  Resources may define their own.
         *
         * @param defaultControllerMethodAccess
         * @return
         */
        public Builder controllerMethodAccess(ControllerMethodAccess defaultControllerMethodAccess) {
            this.defaultControllerMethodAccess = defaultControllerMethodAccess;
            return me();
        }

        /**
         * Provide ParameterConverter implementations to be used for binding
         *
         * @param parameterConverters
         * @return
         */
        public Builder parameterConverters(ParameterConverter<?, ?>... parameterConverters) {
            this.parameterConverters = parameterConverters;
            return me();
        }

        /**
         * If set to true default Up! {@link ParameterConverter} implementations will not be used
         *
         * @param excludeDefaultParameterConverters
         */
        public Builder excludeDefaultParameterConverters(boolean excludeDefaultParameterConverters) {
            this.excludeDefaultParameterConverters = excludeDefaultParameterConverters;
            return me();
        }

        /**
         * Overrides default {@link RequestObjectFactory}
         *
         * @param requestObjectFactory
         * @return
         */
        public Builder requestObjectFactory(RequestObjectFactory requestObjectFactory) {
            this.requestObjectFactory = requestObjectFactory;
            return me();
        }

        /**
         * Overrides default {@link MappedClassFactory}
         *
         * @param mappedClassFactory
         * @return
         */
        public Builder mappedClassFactory(MappedClassFactory mappedClassFactory) {
            this.mappedClassFactory = mappedClassFactory;
            return me();
        }

        /**
         * If true, default filters ({@link NotFoundFilter}, etc) will be excluded
         * from default filters
         *
         * @param excludeFrameworkFilters
         * @return
         */
        public Builder excludeFrameworkFilters(boolean excludeFrameworkFilters) {
            this.excludeFrameworkFilters = excludeFrameworkFilters;
            return me();
        }

        /**
         * Define default service filters to be used for resources relying on filter based services.
         * This will add to default Up! filters unless, {@link #excludeFrameworkFilters(boolean)}
         * is set to true
         *
         * @param filters
         * @return
         */
        public Builder defaultServiceFilters(Object... filters) {
            this.defaultServiceFilters = filters;
            return me();
        }

        public Builder defaultPagination(Pagination defaultPagination) {
            this.defaultPagination = defaultPagination;
            return me();
        }

        public Builder defaultPagination(Integer pageLimit, Integer pageOffset, boolean pagingDisabled, boolean withTotalsDisabled) {
            return defaultPagination(new Pagination(pageLimit, pageOffset, pagingDisabled, withTotalsDisabled));
        }

        public Builder defaultPagination(Integer pageLimit) {
            return defaultPagination(pageLimit, 0, false, false);
        }

        public Builder validator(Validator validator) {
            this.validator = validator;
            return me();
        }

        /**
         * Default implementation to be used when resource does not specify it's own implementation
         *
         * @param restrictedFieldsProvider
         * @return
         */
        public Builder defaultRestrictedFieldsProvider(ResourcePathsProvider restrictedFieldsProvider) {
            this.defaultRestrictedFieldsProvider = restrictedFieldsProvider;
            return me();
        }

        /**
         * Default implementation to be used when resource does not specify it's own implementation
         *
         * @param defaultSparseFieldsProvider
         * @return
         */
        public Builder defaultSparseFieldsProvider(ResourcePathsProvider defaultSparseFieldsProvider) {
            this.defaultSparseFieldsProvider = defaultSparseFieldsProvider;
            return me();
        }

        /**
         * The default base path for all resources
         *
         * @param basePath
         * @return
         */
        public Builder basePath(String basePath) {
            this.basePath = basePath;
            return me();
        }

        public RegistrySettings build() {
            String[] packagesToScan = this.packagesToScan;
            if (ArrayUtils.isEmpty(packagesToScan)) {
                packagesToScan = new String[]{"com"};
            }
            Comparator<MappedField<?>> mappedFieldOrderComparator = this.mappedFieldOrderComparator;
            if (mappedFieldOrderComparator == null) {
                mappedFieldOrderComparator = new MappedFieldComparator();
            }
            MappedFieldBuilderVisitor[] mappedFieldVisitors = this.mappedFieldVisitors;
            if (ArrayUtils.isEmpty(mappedFieldVisitors)) {
                List<MappedFieldBuilderVisitor> visitors = new ArrayList<MappedFieldBuilderVisitor>();
                visitors.add(new IdentityByConventionMappedFieldBuilderVisitor());
                if (AutoDetectConstants.JACKSON2_EXISTS) {
                    visitors.add(new JacksonMappedFieldBuilderVisitor());
                }
                mappedFieldVisitors = visitors.toArray(new MappedFieldBuilderVisitor[0]);
            }
            MappedFieldFactory mappedFieldFactory = this.mappedFieldFactory;
            if (mappedFieldFactory == null) {
                mappedFieldFactory = new DefaultMappedFieldFactory(mappedFieldVisitors);
            }

            ErrorFactory errorFactory = this.errorFactory;
            if (errorFactory == null) {
                errorFactory = new DefaultErrorFactory();
            }
            RequestObjectFactory requestObjectFactory = this.requestObjectFactory;
            if (requestObjectFactory == null) {
                requestObjectFactory = new DefaultRequestObjectFactory();
            }

            Object[] defaultServiceFilters = this.defaultServiceFilters;

            if (!excludeFrameworkFilters) {
                defaultServiceFilters = ArrayUtils.addAll(defaultServiceFilters
                        , new BulkOperationByQueryFilter()
                        , new ImmutableFieldValidationFilter()
                        , new IncludeFilter()
                        , new NotFoundFilter()
                        , new RelationshipValidationFilter()
                        , new SequencedIdValidationFilter()
                        , new CaseInsensitiveSearchFieldFilter());

                Validator javaxValidations = this.validator;
                if (javaxValidations == null) {
                    try {
                        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                        javaxValidations = factory.getValidator();
                    } catch (Exception p) {
                        log.warn("Unable to add JavaxValidationFilter, because no Bean Validation provider could be found. Add a provider like Hibernate Validator (RI) to your classpath.");
                    }
                }
                if (javaxValidations != null) {
                    defaultServiceFilters = ArrayUtils.add(
                            defaultServiceFilters, new JavaxValidationFilter(javaxValidations));
                }
            }

            ResourceRegistryRepository resourceRegistryMap = this.resourceRegistryMap;
            if (resourceRegistryMap == null) {
                resourceRegistryMap = new DefaultResourceRegistryRepository();
            }

            ParameterConverterFactory parameterConverterFactory = new ParameterConverterFactory(errorFactory, !excludeDefaultParameterConverters, parameterConverters);

            ControllerMethodAccess defaultControllerMethodAccess = this.defaultControllerMethodAccess;
            if (defaultControllerMethodAccess == null) {
                defaultControllerMethodAccess = ControllerMethodAccess.builder().setAllEnabled().build();
            }

            ServiceMethodAccess defaultServiceMethodAccess = this.defaultServiceMethodAccess;
            if (defaultServiceMethodAccess == null) {
                defaultServiceMethodAccess = new ServiceMethodAccess.Builder().setAllEnabled().build();
            }

            Pagination pagination = defaultPagination;
            if (pagination == null) {
                pagination = new Pagination(10, 0, false, false);
            }

            ResourcePathsProvider defaultSparseFields = this.defaultSparseFieldsProvider;
            if (defaultSparseFields == null) {
                defaultSparseFields = AllResourcePathsProvider.getDefaultSparseFieldsProvider();
            }

            ResourcePathsProvider restrictedFields = this.defaultRestrictedFieldsProvider;
            if (restrictedFields == null) {
                restrictedFields = new EmptyResourcePathsProvider();
            }

            String basePath = Resource.cleanBasePath(this.basePath);
            if (basePath == null) {
                basePath = "/";
            }

            return new RegistrySettings(primaryRegistry, resourceRegistryMap, mappedClassFactory, packagesToScan
                    , mappedFieldFactory, mappedFieldVisitors, mappedFieldOrderComparator
                    , defaultControllerMethodAccess, defaultServiceMethodAccess, repositoryFactory
                    , errorFactory, requestObjectFactory, methodArgumentFactory, parameterConverterFactory
                    , defaultServiceFilters, pagination, defaultSparseFields, restrictedFields, basePath);
        }
    }

    private static class MappedFieldComparator implements Comparator<MappedField<?>> {

        public int compare(MappedField<?> a, MappedField<?> b) {
            if (a == null) return 1;
            if (a.isIdentifier()) {
                return -10;
            }
            if (b == null) return -1;
            if (b.isIdentifier()) {
                return 1;
            }

            if (a.getApiName() != null) {
                if (b.getApiName() != null) {
                    return a.getApiName().compareTo(b.getApiName());
                }
            }
            if (a.getBeanName() == null) return 1;
            if (b.getBeanName() == null) return 1;
            return a.getBeanName().compareTo(b.getBeanName());
        }

    }

}
